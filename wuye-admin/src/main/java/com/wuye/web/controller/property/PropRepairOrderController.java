package com.wuye.web.controller.property;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wuye.common.annotation.Log;
import com.wuye.common.core.controller.BaseController;
import com.wuye.common.core.domain.AjaxResult;
import com.wuye.common.core.page.TableDataInfo;
import com.wuye.common.enums.BusinessType;
import com.wuye.common.core.domain.entity.SysRole;
import com.wuye.common.core.domain.entity.SysUser;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.common.exception.ServiceException;
import com.wuye.system.domain.PropRepairEvaluation;
import com.wuye.system.domain.PropRepairOrder;
import com.wuye.system.domain.PropRepairOrderLog;
import com.wuye.system.service.IPropRepairOrderLogService;
import com.wuye.system.service.IPropRepairOrderService;
import com.wuye.system.service.ISysRoleService;
import com.wuye.system.service.ISysUserService;

/**
 * 报修工单 REST 接口。
 *
 * 对外提供工单查询、提交、受理、驳回、分配、维修、确认、返工和操作记录等入口。
 * 控制器按角色限定数据范围，服务层再校验工单归属和状态，避免只靠页面按钮控制权限。
 */
@RestController
@RequestMapping("/property/order")
public class PropRepairOrderController extends BaseController
{
    @Autowired
    private IPropRepairOrderService orderService;

    @Autowired
    private IPropRepairOrderLogService orderLogService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    // 根据管理、维修和业主身份附加查询范围，返回当前账号可见工单。
    @PreAuthorize("@ss.hasPermi('property:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(PropRepairOrder order)
    {
        // 管理员看全量；维修人员只看分配给自己的工单；其他账号按业主本人过滤。
        if (!isOrderManager())
        {
            if (isRepairWorker())
            {
                order.setRepairUserId(getUserId());
            }
            else
            {
                order.setOwnerId(getUserId());
            }
        }
        startPage();
        List<PropRepairOrder> list = orderService.selectOrderList(order);
        return getDataTable(list);
    }

    // 查询工单详情并校验数据归属，供只读详情页和编辑回显复用。
    @PreAuthorize("@ss.hasPermi('property:order:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        PropRepairOrder order = orderService.selectOrderById(orderId);
        if (order == null)
        {
            return success(null);
        }
        checkOrderAccess(order);
        return success(order);
    }

    // 使用登录用户身份创建工单，拒绝信任客户端提交的业主编号。
    @PreAuthorize("@ss.hasPermi('property:order:add')")
    @Log(title = "报修工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PropRepairOrder order)
    {
        // 业主身份来自登录上下文，不接受前端伪造的 ownerId。
        order.setOwnerId(getUserId());
        order.setOwnerName(getUsername());
        order.setCreateBy(getUsername());
        return toAjax(orderService.insertOrder(order));
    }

    // 修改待处理或驳回工单，服务层按状态机控制允许更新的字段。
    @PreAuthorize("@ss.hasPermi('property:order:edit') or @ss.hasAnyRoles('property_owner,property_manager,system_admin')")
    @Log(title = "报修工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PropRepairOrder order) { order.setUpdateBy(getUsername()); return toAjax(orderService.updateOrder(order)); }

    // 批量删除工单仅供具备管理权限的账号使用。
    @PreAuthorize("@ss.hasPermi('property:order:remove')")
    @Log(title = "报修工单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable("orderIds") Long[] orderIds) { return toAjax(orderService.deleteOrderByIds(orderIds)); }

    // 物业受理待受理工单，进入正式处理流程。
    @PreAuthorize("@ss.hasPermi('property:order:accept')")
    @PutMapping("/{orderId}/accept")
    public AjaxResult accept(@PathVariable("orderId") Long orderId)
    {
        ensureOrderManager();
        return toAjax(orderService.accept(orderId));
    }

    // 物业驳回不完整工单，并保存原因供业主修改后重新提交。
    @PreAuthorize("@ss.hasPermi('property:order:reject')")
    @PutMapping("/{orderId}/reject")
    public AjaxResult reject(@PathVariable("orderId") Long orderId, @RequestBody Map<String, String> body)
    {
        ensureOrderManager();
        return toAjax(orderService.reject(orderId, body.get("reason")));
    }

    // 将已受理或返工工单分配给有效维修人员。
    @PreAuthorize("@ss.hasPermi('property:order:assign')")
    @PutMapping("/{orderId}/assign")
    public AjaxResult assign(@PathVariable("orderId") Long orderId, @RequestBody PropRepairOrder order)
    {
        ensureOrderManager();
        return toAjax(orderService.assign(orderId, order.getRepairUserId(), order.getRepairUserName()));
    }

    // 查询可分配的正常维修人员，避免停用账号继续接收任务。
    @PreAuthorize("@ss.hasPermi('property:order:assign')")
    @GetMapping("/repair-users")
    public AjaxResult repairUsers()
    {
        ensureOrderManager();
        // 仅返回正常的维修人员角色及正常用户，避免将停用账号分配到新工单。
        SysRole queryRole = new SysRole();
        queryRole.setRoleKey("repair_worker");
        queryRole.setStatus("0");
        List<SysRole> roles = roleService.selectRoleList(queryRole);
        for (SysRole role : roles)
        {
            if ("repair_worker".equals(role.getRoleKey()))
            {
                SysUser queryUser = new SysUser();
                queryUser.setRoleId(role.getRoleId());
                queryUser.setStatus("0");
                return success(userService.selectAllocatedList(queryUser));
            }
        }
        return success(List.of());
    }

    // 维修人员接单后开始处理，服务层验证当前账号就是被分配人员。
    @PreAuthorize("@ss.hasPermi('property:order:start') or @ss.hasAnyRoles('repair_worker,property_manager,system_admin')")
    @PutMapping("/{orderId}/start")
    public AjaxResult start(@PathVariable("orderId") Long orderId) { return toAjax(orderService.start(orderId)); }

    // 提交维修结果和现场图片，将工单流转到业主确认阶段。
    @PreAuthorize("@ss.hasPermi('property:order:finish') or @ss.hasAnyRoles('repair_worker,property_manager,system_admin')")
    @PutMapping("/{orderId}/finish")
    public AjaxResult finish(@PathVariable("orderId") Long orderId, @RequestBody PropRepairOrder order) { return toAjax(orderService.finish(orderId, order.getFinishResult(), order.getFinishImages())); }

    // 业主确认维修结果并在同一事务中生成服务评价。
    @PreAuthorize("@ss.hasPermi('property:order:confirm') or @ss.hasAnyRoles('property_owner,system_admin')")
    @PutMapping("/{orderId}/confirm")
    public AjaxResult confirm(@PathVariable("orderId") Long orderId, @RequestBody PropRepairEvaluation evaluation)
    {
        // 确认维修结果会生成评价，因此在进入事务前再次确认当前用户就是工单业主。
        PropRepairOrder order = orderService.selectOrderById(orderId);
        if (order == null)
        {
            throw new ServiceException("报修工单不存在");
        }
        if (!getUserId().equals(order.getOwnerId()))
        {
            throw new ServiceException("只有工单所属业主可以确认维修结果");
        }
        evaluation.setCreateBy(getUsername());
        return toAjax(orderService.confirm(orderId, evaluation));
    }

    // 取消尚未受理的工单，阻止处理中工单被直接终止。
    @PreAuthorize("@ss.hasPermi('property:order:cancel') or @ss.hasAnyRoles('property_owner,property_manager,system_admin')")
    @PutMapping("/{orderId}/cancel")
    public AjaxResult cancel(@PathVariable("orderId") Long orderId) { return toAjax(orderService.cancel(orderId)); }

    // 业主对维修结果不满意时发起返工，并保存返工原因。
    @PreAuthorize("@ss.hasPermi('property:order:rework') or @ss.hasAnyRoles('property_owner,property_manager,system_admin')")
    @PutMapping("/{orderId}/rework")
    public AjaxResult rework(@PathVariable("orderId") Long orderId, @RequestBody Map<String, String> body) { return toAjax(orderService.rework(orderId, body.get("reason"))); }

    // 返回按时间倒序排列的工单操作记录，并复用详情数据权限校验。
    @PreAuthorize("@ss.hasPermi('property:order:query')")
    @GetMapping("/{orderId}/logs")
    public AjaxResult logs(@PathVariable("orderId") Long orderId)
    {
        PropRepairOrder order = orderService.selectOrderById(orderId);
        if (order == null)
        {
            return success(List.of());
        }
        checkOrderAccess(order);
        List<PropRepairOrderLog> logs = orderLogService.selectLogsByOrderId(orderId);
        return success(logs);
    }

    // 按选定周期汇总看板指标，保证各图表采用同一统计时间范围。
    @PreAuthorize("@ss.hasPermi('property:dashboard:list')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard(@RequestParam(name = "period", defaultValue = "month") String period)
    {
        return success(orderService.selectDashboard(period));
    }

    private void checkOrderAccess(PropRepairOrder order)
    {
        // 详情和操作记录复用同一归属校验，防止通过工单编号直接读取他人数据。
        if (order == null || isOrderManager())
        {
            return;
        }
        Long userId = getUserId();
        if (isRepairWorker())
        {
            if (order.getRepairUserId() == null || !userId.equals(order.getRepairUserId()))
            {
                throw new ServiceException("只有已分配的维修人员可以查看此工单");
            }
            return;
        }
        if (order.getOwnerId() == null || !userId.equals(order.getOwnerId()))
        {
            throw new ServiceException("只有工单所属业主可以查看此工单");
        }
    }

    private boolean isOrderManager()
    {
        // 集中定义可管理全量工单的角色，避免各接口判断标准不一致。
        return SecurityUtils.isAdmin()
                || SecurityUtils.hasRole("system_admin")
                || SecurityUtils.hasRole("property_manager");
    }

    private boolean isRepairWorker()
    {
        // 维修角色用于限制工单列表和详情到当前分配人员。
        return SecurityUtils.hasRole("repair_worker");
    }

    private void ensureOrderManager()
    {
        // 状态管理入口统一执行角色检查，形成注解之外的第二层防护。
        if (!isOrderManager())
        {
            throw new ServiceException("只有物业管理员可以执行此操作");
        }
    }
}
