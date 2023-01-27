package com.chang.yebserver.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chang.yebserver.pojo.Employee;
import com.chang.yebserver.pojo.MailConstants;
import com.chang.yebserver.pojo.MailLog;
import com.chang.yebserver.service.EmployeeService;
import com.chang.yebserver.service.MailLogService;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邮件发送的定时任务
 */

@Component
public class MailTask {

    @Autowired
    private MailLogService mailLogService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 邮件发送定时任务
     * 10秒执行一次
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void mailTask(){
        //获取所有处于投递中的消息（status=0），并且重试时间小于当前时间（也就是当前时间达到了重试时间）
        List<MailLog> list = mailLogService.list(new QueryWrapper<MailLog>().eq("status", MailConstants.DELIVERING).lt("tryTime", LocalDateTime.now()));

        list.forEach(mailLog -> {
            //如果重试次数超过3次，更新状态为投递失败，不再重试
            if (3<=mailLog.getCount()){
                mailLogService.update(new UpdateWrapper<MailLog>().set("status",MailConstants.FAILURE).eq("msgId",mailLog.getMsgId()));
            }
            //如果没有，重试次数+1，设置更新时间，设置重试时间（当前时间加延时时间）
            mailLogService.update(new UpdateWrapper<MailLog>().set("count",mailLog.getCount()+1).set("updateTime",LocalDateTime.now()).set("tryTime",LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT)).eq("msgId",mailLog.getMsgId()));

            //获得Employee对象，重新发送到RabbitMQ
            Employee emp = employeeService.getEmployee(mailLog.getEid()).get(0);
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME,MailConstants.MAIL_QUEUE_NAME,emp,new CorrelationData(mailLog.getMsgId()));
        });
    }
}
