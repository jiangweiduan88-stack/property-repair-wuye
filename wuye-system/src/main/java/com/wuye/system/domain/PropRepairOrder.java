package com.wuye.system.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wuye.common.core.domain.BaseEntity;

/**
 * 报修工单实体，保存从提交到完成的完整业务状态和维修结果。
 * exist=false 字段来自关联查询，仅用于展示业主、房屋、分类和维修人员信息。
 */
@TableName("prop_repair_order")
public class PropRepairOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;
    private String orderNo;
    private Long ownerId;
    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private String ownerPhone;
    private Long roomId;
    @TableField(exist = false)
    private String roomText;
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    private String title;
    private String content;
    private String images;
    /** 0待受理、1已受理、2已分配、3维修中、4待确认、5完成、6驳回、7取消、8返工中。 */
    private String status;
    private Long repairUserId;
    @TableField(exist = false)
    private String repairUserName;
    private String rejectReason;
    private String finishResult;
    private String finishImages;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date acceptTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date assignTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRoomText() { return roomText; }
    public void setRoomText(String roomText) { this.roomText = roomText; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getRepairUserId() { return repairUserId; }
    public void setRepairUserId(Long repairUserId) { this.repairUserId = repairUserId; }
    public String getRepairUserName() { return repairUserName; }
    public void setRepairUserName(String repairUserName) { this.repairUserName = repairUserName; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public String getFinishResult() { return finishResult; }
    public void setFinishResult(String finishResult) { this.finishResult = finishResult; }
    public String getFinishImages() { return finishImages; }
    public void setFinishImages(String finishImages) { this.finishImages = finishImages; }
    public Date getAcceptTime() { return acceptTime; }
    public void setAcceptTime(Date acceptTime) { this.acceptTime = acceptTime; }
    public Date getAssignTime() { return assignTime; }
    public void setAssignTime(Date assignTime) { this.assignTime = assignTime; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }
    public Date getConfirmTime() { return confirmTime; }
    public void setConfirmTime(Date confirmTime) { this.confirmTime = confirmTime; }
}
