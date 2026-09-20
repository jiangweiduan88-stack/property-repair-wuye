package com.wuye.system.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuye.system.domain.PropRepairOrder;
import com.wuye.system.domain.PropRepairStats;

/**
 * 工单持久化接口。
 * 除常规读写外，还提供带预期状态的更新和看板聚合查询，减少业务层拼装 SQL。
 */
public interface PropRepairOrderMapper extends BaseMapper<PropRepairOrder>
{
    List<PropRepairOrder> selectOrderList(PropRepairOrder order);
    PropRepairOrder selectOrderById(Long orderId);
    int insertOrder(PropRepairOrder order);
    int updateOrder(PropRepairOrder order);
    /** 仅当数据库中的状态仍等于 expectedStatus 时更新，用于并发流程保护。 */
    int updateOrderIfStatus(@Param("order") PropRepairOrder order, @Param("expectedStatus") String expectedStatus);
    int deleteOrderByIds(Long[] orderIds);
    PropRepairStats selectRepairStats(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    List<PropRepairOrder> selectStatusStats(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    List<PropRepairOrder> selectCategoryStats(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    List<PropRepairOrder> selectRepairUserStats(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
