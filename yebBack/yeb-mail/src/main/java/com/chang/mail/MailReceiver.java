package com.chang.mail;

import com.chang.yebserver.pojo.Employee;
import com.chang.yebserver.pojo.MailConstants;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;

/**
 * RabbitMq消息接收者，接收到消息后，再发送邮件
 */
@Component
public class MailReceiver {

    //打印日志
    private static final Logger LOGGER = LoggerFactory.getLogger(MailReceiver.class);

    //引入邮件发送
    @Autowired
    private JavaMailSender javaMailSender;

    //引入邮件配置
    @Autowired
    private MailProperties mailProperties;

    //引入模板引擎
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private RedisTemplate redisTemplate;

    //使用注解引入对RabbitMQ的队列监听，handler里面接收传递过来的Employee对象
    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void handler(Message message, Channel channel){

        //不再是直接获取Employee对象，而是通过message获取并强转
        Employee employee = (Employee)message.getPayload();

        //获取消息序号
        MessageHeaders headers = message.getHeaders();
        Long tag =(long) headers.get(AmqpHeaders.DELIVERY_TAG);

        //获取发送消息时存放在CorrelationData中的id
        String msgId = (String) headers.get("spring_returned_message_correlation");

        //使用redis以hash形式存储
        HashOperations hashOperations = redisTemplate.opsForHash();


        try {
            if(hashOperations.entries("mail_log").containsKey(msgId)){
                LOGGER.info("消息已经被消费===============>{}",msgId);
                /**
                 * 手动确认消息
                 * tag：消息序号
                 * multiple：是否确认多条，如果为false，则仅确认1条
                 */
                channel.basicAck(tag,false);
                return;
            }

            //创建消息
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg);
            //设置发件人
            helper.setFrom(mailProperties.getUsername());
            //设置收件人
            helper.setTo(employee.getEmail());
            //设置主题
            helper.setSubject("入职欢迎邮件");
            //发送日期
            helper.setSentDate(new Date());
            //邮件内容
            Context context = new Context();
            context.setVariable("name", employee.getName());
            context.setVariable("posName", employee.getPosition().getName());
            context.setVariable("joblevelName", employee.getJoblevel().getName());
            context.setVariable("departmentName", employee.getDepartment().getName());
            //将邮件内容设置到模板引擎
            String mail = templateEngine.process("mail", context);
            //设置内容
            helper.setText(mail, true);
            //发送邮件
            javaMailSender.send(msg);
            LOGGER.info("邮件发送成功");
            //邮件发送成功，将消息Id存入redis中
            hashOperations.put("mail_log",msgId,"OK");
            //信道手动确认
            channel.basicAck(tag,false);
        } catch (Exception e) {
            /**
             * 手动确认消息
             * tag：消息序号
             * multiple：是否确认多条，如果为false，则仅确认1条
             * requeue：是否重回队列
             */
            try {
                channel.basicNack(tag,false,true);
            } catch (IOException ex) {
                LOGGER.error("邮件发送失败=========>{}", e.getMessage());
            }
            //日志打印错误信息
            LOGGER.error("邮件发送失败=========>{}", e.getMessage());
        }
    }
}
