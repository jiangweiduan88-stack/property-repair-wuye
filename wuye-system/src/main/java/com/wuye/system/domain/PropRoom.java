package com.wuye.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuye.common.core.domain.BaseEntity;

/**
 * 房屋实体，维护楼栋、单元、房号和业主的绑定关系。
 * 标记为 exist=false 的字段仅用于关联查询和页面展示，不会直接写入房屋表。
 */
@TableName("prop_room")
public class PropRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "room_id", type = IdType.AUTO)
    private Long roomId;
    private Long buildingId;
    @TableField(exist = false)
    private String buildingName;
    private String unitNo;
    private String roomNo;
    private Long ownerId;
    private String ownerName;
    @TableField(exist = false)
    private String ownerNickName;
    @TableField(exist = false)
    private String loginAccount;
    @TableField(exist = false)
    private String ownerPhone;
    private String status;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getUnitNo() { return unitNo; }
    public void setUnitNo(String unitNo) { this.unitNo = unitNo; }
    public String getRoomNo() { return roomNo; }
    public void setRoomNo(String roomNo) { this.roomNo = roomNo; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerNickName() { return ownerNickName; }
    public void setOwnerNickName(String ownerNickName) { this.ownerNickName = ownerNickName; }
    public String getLoginAccount() { return loginAccount; }
    public void setLoginAccount(String loginAccount) { this.loginAccount = loginAccount; }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
