package com.wuye.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuye.common.core.domain.BaseEntity;

/** 服务评价实体，关联工单、业主和维修人员，并保存评分与评价内容。 */
@TableName("prop_repair_evaluation")
public class PropRepairEvaluation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "evaluation_id", type = IdType.AUTO)
    private Long evaluationId;
    private Long orderId;
    private String orderNo;
    private Long ownerId;
    private Long repairUserId;
    private Integer score;
    private String content;

    public Long getEvaluationId() { return evaluationId; }
    public void setEvaluationId(Long evaluationId) { this.evaluationId = evaluationId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Long getRepairUserId() { return repairUserId; }
    public void setRepairUserId(Long repairUserId) { this.repairUserId = repairUserId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
