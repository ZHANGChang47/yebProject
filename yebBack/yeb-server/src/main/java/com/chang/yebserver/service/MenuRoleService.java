package com.chang.yebserver.service;

import com.chang.yebserver.pojo.MenuRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chang.yebserver.respPojo.RespBean;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhangChang
 * @since 2023-01-18
 */
public interface MenuRoleService extends IService<MenuRole> {

    /**
     * 更新角色菜单
     * @param rid
     * @param mids
     * @return
     */
    RespBean updateMenuRole(Integer rid, Integer[] mids);
}
