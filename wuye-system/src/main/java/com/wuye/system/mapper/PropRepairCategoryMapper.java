package com.wuye.system.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuye.system.domain.PropRepairCategory;

/** 报修分类持久化接口，集中承载分类查询和维护 SQL。 */
public interface PropRepairCategoryMapper extends BaseMapper<PropRepairCategory>
{
    List<PropRepairCategory> selectCategoryList(PropRepairCategory category);
    PropRepairCategory selectCategoryById(Long categoryId);
    int insertCategory(PropRepairCategory category);
    int updateCategory(PropRepairCategory category);
    int deleteCategoryByIds(Long[] categoryIds);
}
