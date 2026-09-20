package com.wuye.web.controller.common;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wuye.common.core.domain.AjaxResult;
import com.wuye.common.core.domain.entity.SysDept;
import com.wuye.system.mapper.SysDeptMapper;

/**
 * 登录页物业服务中心联系方式。
 *
 * <p>忘记密码发生在用户登录前，因此该接口只返回指定物业部门的公开联系字段，
 * 不返回部门负责人以外的组织或账号信息。集中从部门资料读取可以避免在前端写死电话、
 * 邮箱，后续物业人员调整时只需维护一处数据。</p>
 */
@RestController
public class ServiceCenterController
{
    private static final String SERVICE_CENTER_NAME = "幸福社区物业服务中心";

    @Autowired
    private SysDeptMapper deptMapper;

    @GetMapping("/serviceCenter/contact")
    public AjaxResult getContact()
    {
        // 使用固定的服务中心名称限定查询范围，防止匿名接口枚举全部组织架构。
        SysDept dept = deptMapper.selectPublicContactByName(SERVICE_CENTER_NAME);
        if (dept == null)
        {
            return AjaxResult.error("暂无法获取物业服务中心联系信息");
        }

        // 通过白名单组装公开字段，避免直接序列化 SysDept 泄露内部管理信息。
        Map<String, String> contact = new HashMap<>();
        contact.put("deptName", dept.getDeptName());
        contact.put("leader", dept.getLeader());
        contact.put("phone", dept.getPhone());
        contact.put("email", dept.getEmail());
        return AjaxResult.success(contact);
    }
}
