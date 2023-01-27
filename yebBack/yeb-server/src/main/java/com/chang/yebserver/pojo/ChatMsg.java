package com.chang.yebserver.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 消息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ChatMsg {
    //谁发送的
    private String from;
    //发给谁
    private String to;
    //内容是什么
    private String content;
    //创建时间
    private LocalDateTime date;
    //发送人的昵称
    private String fromNickName;
}
