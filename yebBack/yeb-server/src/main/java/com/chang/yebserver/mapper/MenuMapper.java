package com.chang.yebserver.mapper;

import com.chang.yebserver.pojo.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhangChang
 * @since 2023-01-18
 */
@Repository
public interface MenuMapper extends BaseMapper<Menu> {

    /*
    根据用户ID查询菜单列表
     */
    List<Menu> getMenusByAdminId(Integer id);

    /**
     通过角色获取菜单列表
     */
    List<Menu> getAllMenusWithRole();

    /**
     * 查询所有菜单
     * @return
     */
    List<Menu> getAllMenus();
}
