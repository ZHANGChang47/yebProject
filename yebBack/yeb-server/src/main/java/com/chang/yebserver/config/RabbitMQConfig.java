package com.chang.yebserver.config;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chang.yebserver.pojo.MailConstants;
import com.chang.yebserver.pojo.MailLog;
import com.chang.yebserver.service.MailLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    //日志
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConfig.class);

    //引入连接工厂
    @Autowired
    private CachingConnectionFactory cachingConnectionFactory;

    @Autowired
    private MailLogService mailLogService;

    //配置RabbitMQ消息确认回调以及失败回调
    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        /**
         * 1、消息发送确认回调
         * data：消息的唯一标识
         * ack：确认结果
         * cause：失败的原因
         */
        rabbitTemplate.setConfirmCallback((data,ack,cause)->{
            String msgId = data.getId();
            if(ack){
                LOGGER.info("{}==========>消息发送成功",msgId);
                //成功要去更新数据库
                mailLogService.update(new UpdateWrapper<MailLog>().set("status",MailConstants.SUCCESS).eq("msgId",msgId));
            }else {
                LOGGER.info("{}==========>消息发送失败",msgId);
            }
        });

        /**
         * 2、消息发送失败回调，比如router不到queue时回调
         * msg：消息主题
         * repCode：响应码
         * repText：响应描述
         * exchange：交换机
         * routingKey：路由键
         */
        rabbitTemplate.setReturnCallback((msg,repCode,repText,exchange,routingKey)->{
            LOGGER.info("{}==========>消息发送到queue时失败",msg.getBody());
        });

        return rabbitTemplate;
    }

    //创建队列
    @Bean
    public Queue queue(){
        return new Queue(MailConstants.MAIL_QUEUE_NAME);
    }

    //创建交换机
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(MailConstants.MAIL_EXCHANGE_NAME);
    }

    //绑定队列与交换机
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(directExchange()).with(MailConstants.MAIL_ROUTING_KEY_NAME);
    }
}
