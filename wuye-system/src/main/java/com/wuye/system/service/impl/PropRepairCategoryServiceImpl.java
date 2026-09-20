package com.wuye.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wuye.common.exception.ServiceException;
import com.wuye.system.domain.PropRepairCategory;
import com.wuye.system.mapper.PropRepairCategoryMapper;
import com.wuye.system.service.IPropRepairCategoryService;

/**
 * 报修分类业务服务。
 *
 * 集中规范分类名称、排序和状态，并保证分类名称不重复，
 * 有利于工单统计口径统一，避免同类故障被拆分到多个重复分类。
 */
@Service
public class PropRepairCategoryServiceImpl extends ServiceImpl<PropRepairCategoryMapper, PropRepairCategory> implements IPropRepairCategoryService
{
    @Autowired
    private PropRepairCategoryMapper categoryMapper;

    // 按名称和状态等条件查询分类，供管理列表与工单选项复用。
    public List<PropRepairCategory> selectCategoryList(PropRepairCategory category) { return categoryMapper.selectCategoryList(category); }
    // 根据主键读取分类详情，供修改回显和工单合法性检查使用。
    public PropRepairCategory selectCategoryById(Long categoryId) { return categoryMapper.selectCategoryById(categoryId); }
    // 规范名称、排序和状态后新增分类，保持统计口径稳定。
    public int insertCategory(PropRepairCategory category)
    {
        validateCategory(category);
        return categoryMapper.insertCategory(category);
    }

    // 确认分类存在并执行统一校验后更新分类配置。
    public int updateCategory(PropRepairCategory category)
    {
        if (category == null || category.getCategoryId() == null || categoryMapper.selectCategoryById(category.getCategoryId()) == null)
        {
            throw new ServiceException("报修分类不存在");
        }
        validateCategory(category);
        return categoryMapper.updateCategory(category);
    }
    // 批量删除分类，避免循环请求造成部分成功和重复日志。
    public int deleteCategoryByIds(Long[] categoryIds) { return categoryMapper.deleteCategoryByIds(categoryIds); }

    private void validateCategory(PropRepairCategory category)
    {
        // 新增和修改都经过这里，确保所有入口遵循一致的数据质量规则。
        if (category == null)
        {
            throw new ServiceException("报修分类不能为空");
        }
        String name = category.getCategoryName() == null ? "" : category.getCategoryName().trim();
        if (name.isEmpty())
        {
            throw new ServiceException("请填写分类名称");
        }
        if (name.length() > 80)
        {
            throw new ServiceException("分类名称长度不能超过80个字符");
        }
        if (category.getOrderNum() == null || category.getOrderNum() < 0)
        {
            category.setOrderNum(0);
        }
        if (!"0".equals(category.getStatus()) && !"1".equals(category.getStatus()))
        {
            category.setStatus("0");
        }
        category.setCategoryName(name);
        PropRepairCategory query = new PropRepairCategory();
        query.setCategoryName(name);
        for (PropRepairCategory existing : categoryMapper.selectCategoryList(query))
        {
            if (name.equals(existing.getCategoryName())
                    && (category.getCategoryId() == null || !category.getCategoryId().equals(existing.getCategoryId())))
            {
                throw new ServiceException("分类名称已存在");
            }
        }
    }
}
