package com.wuye.common.core.domain.model;

/**
 * 用户注册对象
 * 
 * @author wuye
 */
public class RegisterBody extends LoginBody
{
    /** 业主姓名 */
    private String ownerName;

    /** 手机号码 */
    private String phonenumber;

    /** 楼栋ID */
    private Long buildingId;

    /** 单元 */
    private String unitNo;

    /** 房号 */
    private String roomNo;

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber)
    {
        this.phonenumber = phonenumber;
    }

    public Long getBuildingId()
    {
        return buildingId;
    }

    public void setBuildingId(Long buildingId)
    {
        this.buildingId = buildingId;
    }

    public String getUnitNo()
    {
        return unitNo;
    }

    public void setUnitNo(String unitNo)
    {
        this.unitNo = unitNo;
    }

    public String getRoomNo()
    {
        return roomNo;
    }

    public void setRoomNo(String roomNo)
    {
        this.roomNo = roomNo;
    }

}
