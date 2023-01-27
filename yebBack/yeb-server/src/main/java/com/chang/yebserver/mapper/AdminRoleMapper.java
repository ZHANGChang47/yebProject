package com.chang.yebserver.mapper;

import com.chang.yebserver.pojo.AdminRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chang.yebserver.respPojo.RespBean;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhangChang
 * @since 2023-01-18
 */
@Repository
public interface AdminRoleMapper extends BaseMapper<AdminRole> {

    /**
     * 更新操作员角色
     * @param adminId
     * @param rids
     * @return
     */
    Integer addAdminRole(@Param("adminId") Integer adminId, @Param("rids") Integer[] rids);
}
