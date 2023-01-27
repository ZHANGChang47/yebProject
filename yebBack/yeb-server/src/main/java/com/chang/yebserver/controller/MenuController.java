package com.chang.yebserver.controller;


import com.chang.yebserver.pojo.Menu;
import com.chang.yebserver.service.MenuService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhangChang
 * @since 2023-01-18
 */
@RestController
//查询数据库的表t_menu，菜单一般放在系统管理里面，所以其路由设置为"/system/config"
@RequestMapping("/system/config")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "通过用户id获取菜单列表")
    @GetMapping("/menu")
    public List<Menu> getMenusByAdminId() {
        //之所以没有传入参数，是因为使用security即可在全局范围内获取到用户对象
        return menuService.getMenusByAdminId();
    }

}
