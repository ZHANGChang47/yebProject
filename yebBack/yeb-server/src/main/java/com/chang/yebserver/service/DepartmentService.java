package com.chang.yebserver.service;

import com.chang.yebserver.pojo.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chang.yebserver.respPojo.RespBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ZhangChang
 * @since 2023-01-18
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取所有部门
     * @return
     */
    List<Department> getAllDepartments();

    /**
     * 添加部门
     * @param dep
     * @return
     */
    RespBean addDep(Department dep);

    /**
     * 删除部门
     * @param id
     * @return
     */
    RespBean deleteDep(Integer id);
}
