package com.wuye.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wuye.system.domain.PropRepairOrderLog;
import com.wuye.system.mapper.PropRepairOrderLogMapper;
import com.wuye.system.service.IPropRepairOrderLogService;

/**
 * 工单操作记录服务。
 *
 * 将状态动作、中文标签、操作人和内容统一写入独立日志表，
 * 使工单流转可追溯，同时避免主工单表被大量历史文本污染。
 */
@Service
public class PropRepairOrderLogServiceImpl extends ServiceImpl<PropRepairOrderLogMapper, PropRepairOrderLog> implements IPropRepairOrderLogService
{
    @Autowired
    private PropRepairOrderLogMapper logMapper;

    @Override
    public List<PropRepairOrderLog> selectLogsByOrderId(Long orderId)
    {
        // Mapper 已按操作时间倒序返回，最新流转记录优先展示。
        return logMapper.selectLogsByOrderId(orderId);
    }

    @Override
    public int insertLog(Long orderId, String orderNo, String action, String actionLabel, Long operatorId, String operatorName, String content)
    {
        // 将一次状态动作转换为独立日志实体，避免调用方重复组装审计字段。
        PropRepairOrderLog log = new PropRepairOrderLog();
        log.setOrderId(orderId);
        log.setOrderNo(orderNo);
        log.setAction(action);
        log.setActionLabel(actionLabel);
        log.setOperatorId(operatorId);
        log.setOperatorName(operatorName);
        log.setContent(content != null ? content : "");
        return logMapper.insertLog(log);
    }
}
