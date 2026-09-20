package com.wuye.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wuye.common.exception.ServiceException;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.system.domain.PropRepairEvaluation;
import com.wuye.system.domain.PropRepairOrder;
import com.wuye.system.mapper.PropRepairEvaluationMapper;
import com.wuye.system.mapper.PropRepairOrderMapper;
import com.wuye.system.service.IPropRepairEvaluationService;

/**
 * 服务评价业务服务。
 *
 * 根据当前角色强制追加数据范围，校验工单完成状态、评价唯一性和内容合法性。
 * 即使请求绕过页面，服务层仍能保证用户只能操作与本人相关的评价。
 */
@Service
public class PropRepairEvaluationServiceImpl extends ServiceImpl<PropRepairEvaluationMapper, PropRepairEvaluation> implements IPropRepairEvaluationService
{
    @Autowired
    private PropRepairEvaluationMapper evaluationMapper;

    @Autowired
    private PropRepairOrderMapper orderMapper;

    // 根据当前角色强制覆盖数据范围后查询评价，防止伪造身份条件。
    public List<PropRepairEvaluation> selectEvaluationList(PropRepairEvaluation evaluation)
    {
        PropRepairEvaluation query = evaluation == null ? new PropRepairEvaluation() : evaluation;
        // 覆盖非管理员传入的身份条件，防止通过修改查询参数查看他人评价。
        if (!isManager())
        {
            if (SecurityUtils.hasRole("repair_worker"))
            {
                query.setRepairUserId(SecurityUtils.getUserId());
                query.setOwnerId(null);
            }
            else
            {
                query.setOwnerId(SecurityUtils.getUserId());
                query.setRepairUserId(null);
            }
        }
        return evaluationMapper.selectEvaluationList(query);
    }
    // 根据主键读取评价详情，归属权限由控制器和写操作继续校验。
    public PropRepairEvaluation selectEvaluationById(Long evaluationId) { return evaluationMapper.selectEvaluationById(evaluationId); }

    // 校验工单完成状态、评价唯一性及内容后创建评价。
    public int insertEvaluation(PropRepairEvaluation evaluation)
    {
        if (evaluation == null || evaluation.getOrderId() == null)
        {
            throw new ServiceException("请选择需要评价的报修工单");
        }
        PropRepairOrder order = orderMapper.selectOrderById(evaluation.getOrderId());
        if (order == null || !"5".equals(order.getStatus()))
        {
            throw new ServiceException("只有已完成的报修工单可以评价");
        }
        if (evaluationMapper.selectEvaluationByOrderId(evaluation.getOrderId()) != null)
        {
            // 一个工单只允许一条评价，避免重复提交影响看板平均分。
            throw new ServiceException("此报修工单已经完成评价，请勿重复评价");
        }
        ensureOwnerOrManager(order.getOwnerId());
        validateScore(evaluation.getScore());
        validateContent(evaluation.getContent());
        evaluation.setOrderNo(order.getOrderNo());
        // 关联身份从可信工单读取，不采用前端提交值，确保评价归属不可伪造。
        evaluation.setOwnerId(order.getOwnerId());
        evaluation.setRepairUserId(order.getRepairUserId());
        return evaluationMapper.insertEvaluation(evaluation);
    }

    // 仅允许评价所属业主或管理角色修改评分和内容。
    public int updateEvaluation(PropRepairEvaluation evaluation)
    {
        if (evaluation == null || evaluation.getEvaluationId() == null)
        {
            throw new ServiceException("服务评价不存在");
        }
        PropRepairEvaluation existing = evaluationMapper.selectEvaluationById(evaluation.getEvaluationId());
        if (existing == null)
        {
            throw new ServiceException("服务评价不存在");
        }
        ensureOwnerOrManager(existing.getOwnerId());
        if (evaluation.getScore() != null)
        {
            validateScore(evaluation.getScore());
        }
        validateContent(evaluation.getContent());
        return evaluationMapper.updateEvaluation(evaluation);
    }
    // 删除评价属于管理操作，不向普通业主或维修人员开放。
    public int deleteEvaluationByIds(Long[] evaluationIds)
    {
        ensureManager();
        return evaluationMapper.deleteEvaluationByIds(evaluationIds);
    }

    private void ensureOwnerOrManager(Long ownerId)
    {
        // 数据所有者与管理角色共享维护入口，其余用户直接拒绝。
        if (isManager())
        {
            return;
        }
        if (ownerId == null || !ownerId.equals(SecurityUtils.getUserId()))
        {
            throw new ServiceException("只能维护本人提交的服务评价");
        }
    }

    private void ensureManager()
    {
        // 对高风险删除操作执行独立管理角色校验。
        if (!isManager())
        {
            throw new ServiceException("只有系统管理员或物业管理员可以删除服务评价");
        }
    }

    private boolean isManager()
    {
        // 集中维护评价管理角色，确保查询和写操作判断一致。
        return SecurityUtils.isAdmin()
                || SecurityUtils.hasRole("system_admin")
                || SecurityUtils.hasRole("property_manager");
    }

    private void validateScore(Integer score)
    {
        // 评分限定为一至五星，保证平均分统计具备统一量纲。
        if (score == null || score < 1 || score > 5)
        {
            throw new ServiceException("服务评分必须在1到5分之间");
        }
    }

    private void validateContent(String content)
    {
        // 限制评价文本长度，兼顾可读性和数据库字段容量。
        if (content != null && content.trim().length() > 500)
        {
            throw new ServiceException("评价内容长度不能超过500个字符");
        }
    }
}
