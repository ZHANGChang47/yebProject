package com.chang.yebserver.respPojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RespBean {
    //返回状态码
    private long code;
    //返回提示信息
    private String message;
    //返回对象
    private Object obj;

    /*
    1、定义成功返回方法，参数只有message
     */
    public static RespBean success(String message){
        return new RespBean(200,message,null);
    }

    /*
    2、定义成功返回方法，参数有message与obj
     */
    public static RespBean success(String message,Object obj){
        return new RespBean(200,message,obj);
    }

    /*
    3、定义失败返回方法，参数只有message
     */
    public static RespBean error(String message){
        return new RespBean(500,message,null);
    }

    /*
     4、定义失败返回方法，参数有message与obj
     */
    public static RespBean error(String message,Object obj){
        return new RespBean(500,message,obj);
    }
}
