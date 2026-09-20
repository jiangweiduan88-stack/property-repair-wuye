package com.wuye.system.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuye.system.domain.PropRepairOrderLog;

/** 工单操作记录持久化接口，按工单读取时间线并写入新的流程记录。 */
public interface PropRepairOrderLogMapper extends BaseMapper<PropRepairOrderLog>
{
    List<PropRepairOrderLog> selectLogsByOrderId(Long orderId);
    int insertLog(PropRepairOrderLog log);
}
