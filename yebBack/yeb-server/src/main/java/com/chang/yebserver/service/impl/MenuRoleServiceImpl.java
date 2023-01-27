package com.chang.yebserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chang.yebserver.pojo.MenuRole;
import com.chang.yebserver.mapper.MenuRoleMapper;
import com.chang.yebserver.respPojo.RespBean;
import com.chang.yebserver.service.MenuRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhangChang
 * @since 2023-01-18
 */
@Service
public class MenuRoleServiceImpl extends ServiceImpl<MenuRoleMapper, MenuRole> implements MenuRoleService {

    @Autowired
    private MenuRoleMapper menuRoleMapper;
    /**
     * 更新角色菜单
     *  1、先删除rid下的所有菜单mid
     *  2、然后再设置所有mid
     *  因为涉及到删除最好加上事务注解@Transactional
     */
    @Override
    @Transactional
    public RespBean updateMenuRole(Integer rid, Integer[] mids) {
        menuRoleMapper.delete(new QueryWrapper<MenuRole>().eq("rid",rid));
        if(null == mids || 0==mids.length){
            return RespBean.success("更新成功！");
        }
        //如果mids中有值，那么就需要批量更新
        Integer result = menuRoleMapper.insertRecord(rid, mids);
        if(result == mids.length){
            //通过长度判断是否更新完毕
            return RespBean.success("更新成功！");
        }
        return RespBean.error("更新失败！");
    }

}
