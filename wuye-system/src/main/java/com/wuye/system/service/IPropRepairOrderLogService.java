package com.wuye.system.service;

import java.util.List;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wuye.system.domain.PropRepairOrderLog;

/** 工单操作记录契约，为业务状态变化提供统一审计入口。 */
public interface IPropRepairOrderLogService extends IService<PropRepairOrderLog>
{
    List<PropRepairOrderLog> selectLogsByOrderId(Long orderId);
    int insertLog(Long orderId, String orderNo, String action, String actionLabel, Long operatorId, String operatorName, String content);
}
