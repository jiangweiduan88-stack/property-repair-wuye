package com.wuye.system.service;

import java.util.List;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wuye.system.domain.PropBuilding;

/** 楼栋业务契约，隔离控制器与具体数据库实现。 */
public interface IPropBuildingService extends IService<PropBuilding>
{
    List<PropBuilding> selectBuildingList(PropBuilding building);
    PropBuilding selectBuildingById(Long buildingId);
    int insertBuilding(PropBuilding building);
    int updateBuilding(PropBuilding building);
    int deleteBuildingByIds(Long[] buildingIds);
}
