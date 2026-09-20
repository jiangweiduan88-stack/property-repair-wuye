package com.wuye.system.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuye.system.domain.PropRepairEvaluation;

/** 服务评价持久化接口，支持按评价编号、工单编号和角色条件读取数据。 */
public interface PropRepairEvaluationMapper extends BaseMapper<PropRepairEvaluation>
{
    List<PropRepairEvaluation> selectEvaluationList(PropRepairEvaluation evaluation);
    PropRepairEvaluation selectEvaluationById(Long evaluationId);
    PropRepairEvaluation selectEvaluationByOrderId(Long orderId);
    int insertEvaluation(PropRepairEvaluation evaluation);
    int updateEvaluation(PropRepairEvaluation evaluation);
    int deleteEvaluationByIds(Long[] evaluationIds);
}
