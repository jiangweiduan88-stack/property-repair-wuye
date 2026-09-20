package com.wuye.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wuye.common.exception.ServiceException;
import com.wuye.system.domain.PropBuilding;
import com.wuye.system.mapper.PropBuildingMapper;
import com.wuye.system.service.IPropBuildingService;

/**
 * 楼栋业务服务。
 *
 * 在写入数据库前统一处理必填项、长度、楼层数、状态和名称重复校验，
 * 避免不同调用入口产生规则不一致的楼栋数据。
 */
@Service
public class PropBuildingServiceImpl extends ServiceImpl<PropBuildingMapper, PropBuilding> implements IPropBuildingService
{
    @Autowired
    private PropBuildingMapper buildingMapper;

    // 按查询条件返回楼栋列表，分页边界由控制器统一设置。
    public List<PropBuilding> selectBuildingList(PropBuilding building) { return buildingMapper.selectBuildingList(building); }
    // 根据主键读取楼栋，供详情回显和房屋关联校验使用。
    public PropBuilding selectBuildingById(Long buildingId) { return buildingMapper.selectBuildingById(buildingId); }
    // 校验名称、楼层和状态后新增楼栋，避免无效基础资料进入数据库。
    public int insertBuilding(PropBuilding building)
    {
        validateBuilding(building);
        return buildingMapper.insertBuilding(building);
    }

    // 确认记录存在并复用新增校验规则后更新楼栋。
    public int updateBuilding(PropBuilding building)
    {
        if (building == null || building.getBuildingId() == null || buildingMapper.selectBuildingById(building.getBuildingId()) == null)
        {
            throw new ServiceException("楼栋信息不存在");
        }
        validateBuilding(building);
        return buildingMapper.updateBuilding(building);
    }
    // 批量删除楼栋，使用单次数据库调用保证处理结果一致。
    public int deleteBuildingByIds(Long[] buildingIds) { return buildingMapper.deleteBuildingByIds(buildingIds); }

    private void validateBuilding(PropBuilding building)
    {
        // 新增和修改共用同一套校验，减少规则重复及后续维护遗漏。
        if (building == null)
        {
            throw new ServiceException("楼栋信息不能为空");
        }
        String name = building.getBuildingName() == null ? "" : building.getBuildingName().trim();
        if (name.isEmpty())
        {
            throw new ServiceException("请填写楼栋名称");
        }
        if (name.length() > 80)
        {
            throw new ServiceException("楼栋名称长度不能超过80个字符");
        }
        if (building.getFloors() == null || building.getFloors() < 1)
        {
            throw new ServiceException("楼层数必须大于0");
        }
        if (!"0".equals(building.getStatus()) && !"1".equals(building.getStatus()))
        {
            building.setStatus("0");
        }
        building.setBuildingName(name);
        PropBuilding query = new PropBuilding();
        query.setBuildingName(name);
        for (PropBuilding existing : buildingMapper.selectBuildingList(query))
        {
            if (name.equals(existing.getBuildingName())
                    && (building.getBuildingId() == null || !building.getBuildingId().equals(existing.getBuildingId())))
            {
                throw new ServiceException("楼栋名称已存在");
            }
        }
    }
}
