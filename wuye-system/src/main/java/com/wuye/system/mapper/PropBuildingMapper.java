package com.wuye.system.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuye.system.domain.PropBuilding;

/** 楼栋持久化接口，将业务服务与具体 SQL 解耦，便于独立调整查询实现。 */
public interface PropBuildingMapper extends BaseMapper<PropBuilding>
{
    List<PropBuilding> selectBuildingList(PropBuilding building);
    PropBuilding selectBuildingById(Long buildingId);
    int insertBuilding(PropBuilding building);
    int updateBuilding(PropBuilding building);
    int deleteBuildingByIds(Long[] buildingIds);
}
