package com.wuye.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wuye.common.core.domain.BaseEntity;

/** 楼栋实体，保存楼栋名称、地址、层数及启停状态。 */
@TableName("prop_building")
public class PropBuilding extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "building_id", type = IdType.AUTO)
    private Long buildingId;
    private String buildingName;
    private String address;
    private Integer floors;
    private String status;

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
