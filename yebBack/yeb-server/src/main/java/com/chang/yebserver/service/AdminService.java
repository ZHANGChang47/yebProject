package com.chang.yebserver.service;

import com.chang.yebserver.pojo.Admin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chang.yebserver.pojo.Role;
import com.chang.yebserver.respPojo.RespBean;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhangChang
 * @since 2023-01-18
 */
public interface AdminService extends IService<Admin> {
    /*
    登录之后返回Token
     */
    RespBean login(String username, String password, String code, HttpServletRequest request);

    /*
    根据用户名获取用户
     */
    Admin getAdminByUserName(String username);

    /**
     * 根据用户id或者权限列表
     */
    List<Role> getRoles(Integer adminId);

    /**
     * 获取所有操作员
     * @param keywords
     * @return
     */
    List<Admin> getAllAdmins(String keywords);

    /**
     * 更新操作员角色
     * @param adminId
     * @param rids
     * @return
     */
    RespBean updateAdminRole(Integer adminId, Integer[] rids);

    /**
     * 更新用户密码
     * @param oldPass
     * @param pass
     * @param adminId
     * @return
     */
    RespBean updateAdminPassword(String oldPass, String pass, Integer adminId);

    /**
     * 更新用户头像
     * @param url
     * @param id
     * @param authentication
     * @return
     */
    RespBean updateAdminUserFace(String url, Integer id, Authentication authentication);
}
