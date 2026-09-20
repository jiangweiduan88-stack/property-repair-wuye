package com.wuye.system.service;

import java.util.List;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wuye.system.domain.PropRepairEvaluation;

/** 服务评价业务契约，约束评价查询、创建、修改和删除能力。 */
public interface IPropRepairEvaluationService extends IService<PropRepairEvaluation>
{
    List<PropRepairEvaluation> selectEvaluationList(PropRepairEvaluation evaluation);
    PropRepairEvaluation selectEvaluationById(Long evaluationId);
    int insertEvaluation(PropRepairEvaluation evaluation);
    int updateEvaluation(PropRepairEvaluation evaluation);
    int deleteEvaluationByIds(Long[] evaluationIds);
}
