package com.wuye.system.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wuye.system.domain.PropRepairEvaluation;
import com.wuye.system.domain.PropRepairOrder;
import com.wuye.system.domain.PropRepairStats;

/** 工单业务契约，完整描述报修状态流转及看板统计能力。 */
public interface IPropRepairOrderService extends IService<PropRepairOrder>
{
    List<PropRepairOrder> selectOrderList(PropRepairOrder order);
    PropRepairOrder selectOrderById(Long orderId);
    int insertOrder(PropRepairOrder order);
    int updateOrder(PropRepairOrder order);
    int deleteOrderByIds(Long[] orderIds);
    int accept(Long orderId);
    int reject(Long orderId, String reason);
    int assign(Long orderId, Long repairUserId, String repairUserName);
    int start(Long orderId);
    int finish(Long orderId, String finishResult, String finishImages);
    int confirm(Long orderId, PropRepairEvaluation evaluation);
    int cancel(Long orderId);
    int rework(Long orderId, String reason);
    PropRepairStats selectRepairStats();
    Map<String, Object> selectDashboard(String period);
}
