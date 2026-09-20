package com.wuye.system.service;

import java.util.List;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wuye.system.domain.PropRepairCategory;

/** 报修分类业务契约，为工单分类选择和后台维护提供统一入口。 */
public interface IPropRepairCategoryService extends IService<PropRepairCategory>
{
    List<PropRepairCategory> selectCategoryList(PropRepairCategory category);
    PropRepairCategory selectCategoryById(Long categoryId);
    int insertCategory(PropRepairCategory category);
    int updateCategory(PropRepairCategory category);
    int deleteCategoryByIds(Long[] categoryIds);
}
