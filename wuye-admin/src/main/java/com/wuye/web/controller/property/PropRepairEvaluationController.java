package com.wuye.web.controller.property;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wuye.common.annotation.Log;
import com.wuye.common.core.controller.BaseController;
import com.wuye.common.core.domain.AjaxResult;
import com.wuye.common.core.page.TableDataInfo;
import com.wuye.common.enums.BusinessType;
import com.wuye.common.exception.ServiceException;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.system.domain.PropRepairEvaluation;
import com.wuye.system.service.IPropRepairEvaluationService;

/**
 * 服务评价接口。
 *
 * 根据登录角色过滤评价数据：业主查看本人评价，维修人员查看与本人相关的评价，
 * 管理人员查看全部数据。控制器和服务层同时校验归属，形成纵深权限保护。
 */
@RestController
@RequestMapping("/property/evaluation")
public class PropRepairEvaluationController extends BaseController
{
    @Autowired
    private IPropRepairEvaluationService evaluationService;

    // 按角色附加数据范围后分页查询评价，防止跨账号读取服务反馈。
    @PreAuthorize("@ss.hasPermi('property:evaluation:list')")
    @GetMapping("/list")
    public TableDataInfo list(PropRepairEvaluation evaluation)
    {
        // 非管理角色必须附加当前用户条件，不能依赖前端传入 ownerId 进行数据隔离。
        if (!isEvaluationManager())
        {
            if (SecurityUtils.hasRole("repair_worker"))
            {
                evaluation.setRepairUserId(getUserId());
            }
            else
            {
                evaluation.setOwnerId(getUserId());
            }
        }
        startPage();
        return getDataTable(evaluationService.selectEvaluationList(evaluation));
    }

    // 读取评价详情后再次校验归属，避免通过编号直接访问他人数据。
    @PreAuthorize("@ss.hasPermi('property:evaluation:list') or @ss.hasPermi('property:evaluation:edit')")
    @GetMapping("/{evaluationId}")
    public AjaxResult getInfo(@PathVariable("evaluationId") Long evaluationId)
    {
        PropRepairEvaluation evaluation = evaluationService.selectEvaluationById(evaluationId);
        checkEvaluationAccess(evaluation);
        return success(evaluation);
    }

    // 新增评价并保存创建账号，服务层负责验证工单状态和唯一性。
    @PreAuthorize("@ss.hasPermi('property:evaluation:add')")
    @Log(title = "服务评价", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PropRepairEvaluation evaluation) { evaluation.setCreateBy(getUsername()); return toAjax(evaluationService.insertEvaluation(evaluation)); }

    // 修改评价前交由服务层复核当前用户是否拥有数据权限。
    @PreAuthorize("@ss.hasPermi('property:evaluation:edit')")
    @Log(title = "服务评价", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PropRepairEvaluation evaluation) { evaluation.setUpdateBy(getUsername()); return toAjax(evaluationService.updateEvaluation(evaluation)); }

    // 删除评价仅向具备删除权限的管理账号开放。
    @PreAuthorize("@ss.hasPermi('property:evaluation:remove')")
    @Log(title = "服务评价", businessType = BusinessType.DELETE)
    @DeleteMapping("/{evaluationIds}")
    public AjaxResult remove(@PathVariable("evaluationIds") Long[] evaluationIds) { return toAjax(evaluationService.deleteEvaluationByIds(evaluationIds)); }

    private void checkEvaluationAccess(PropRepairEvaluation evaluation)
    {
        // 详情接口再次校验数据归属，防止通过猜测评价编号绕过列表过滤。
        if (evaluation == null || isEvaluationManager())
        {
            return;
        }
        Long userId = getUserId();
        if (SecurityUtils.hasRole("repair_worker"))
        {
            if (!userId.equals(evaluation.getRepairUserId()))
            {
                throw new ServiceException("只能查看与本人相关的服务评价");
            }
            return;
        }
        if (!userId.equals(evaluation.getOwnerId()))
        {
            throw new ServiceException("只能查看本人提交的服务评价");
        }
    }

    private boolean isEvaluationManager()
    {
        // 管理角色集中定义，确保列表、详情和写操作使用同一判断口径。
        return SecurityUtils.isAdmin()
                || SecurityUtils.hasRole("system_admin")
                || SecurityUtils.hasRole("property_manager");
    }
}
