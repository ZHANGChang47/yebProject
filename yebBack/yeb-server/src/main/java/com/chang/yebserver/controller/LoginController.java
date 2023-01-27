package com.chang.yebserver.controller;

import com.chang.yebserver.pojo.Admin;
import com.chang.yebserver.reqPojo.AdminLoginParam;
import com.chang.yebserver.respPojo.RespBean;
import com.chang.yebserver.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@Api(tags = "LoginController")
public class LoginController {

    @Autowired
    private AdminService adminService;

    @ApiOperation(value = "登录之后返回Token")
    @PostMapping("/login")
    public RespBean login(@RequestBody AdminLoginParam adminLoginParam, HttpServletRequest request) {
        return adminService.login(adminLoginParam.getUsername(),adminLoginParam.getPassword(),adminLoginParam.getCode(),request);
    }

    @ApiOperation(value = "获取当前登录用户的信息")
    @GetMapping("/admin/info")
    //登录成功后，用户信息被更新存放到security全文中，可以通过principal获取
    public Admin getAdminInfo(Principal principal){
        if(null==principal){
            return null;
        }
        String username = principal.getName();
        Admin admin = adminService.getAdminByUserName(username);
        //用户密码即使加密也不应该被返回，所以要设置为null
        admin.setPassword(null);
        admin.setRoles(adminService.getRoles(admin.getId()));
        return admin;
    }


    /*
    正常的流程是登录成功后返回token，前端会token放在请求头里面，访问任何接口都会携带这个token令牌，我们通过一个拦截器判断这个token是否合法且有效，只有合法有效才能方位其他的接口，如果非法则无法访问。
    和前端定义好，调用后端退出登录的接口，返回一个"退出成功"以及200的状态码，前端拿到这个200状态码之后直接在请求头中将token删除，之后再调接口就会被拦截器拦截无法访问
    这样做是为了提高系统的响应速度，验证信息不走后端，只在前端比较字符串
     */
    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public RespBean logout(){
        return RespBean.success("注销成功！");
    }
}
