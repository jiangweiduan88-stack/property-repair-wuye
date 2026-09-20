package com.wuye.system.domain;

import java.math.BigDecimal;

/** 看板汇总模型，承载工单总数、待办数、完成数和平均评分。 */
public class PropRepairStats
{
    private Long totalCount;
    private Long pendingCount;
    private Long completedCount;
    private BigDecimal avgScore;

    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
    public Long getPendingCount() { return pendingCount; }
    public void setPendingCount(Long pendingCount) { this.pendingCount = pendingCount; }
    public Long getCompletedCount() { return completedCount; }
    public void setCompletedCount(Long completedCount) { this.completedCount = completedCount; }
    public BigDecimal getAvgScore() { return avgScore; }
    public void setAvgScore(BigDecimal avgScore) { this.avgScore = avgScore; }
}
