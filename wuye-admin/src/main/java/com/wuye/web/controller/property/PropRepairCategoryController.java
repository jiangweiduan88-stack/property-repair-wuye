package com.wuye.web.controller.property;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wuye.common.annotation.Log;
import com.wuye.common.core.controller.BaseController;
import com.wuye.common.core.domain.AjaxResult;
import com.wuye.common.core.page.TableDataInfo;
import com.wuye.common.enums.BusinessType;
import com.wuye.system.domain.PropRepairCategory;
import com.wuye.system.service.IPropRepairCategoryService;

/**
 * 报修分类接口。
 *
 * 管理水电、公共设施等报修类型，并单独提供正常分类选项接口。
 * 列表管理与业务选项分离，可避免停用分类继续出现在新建工单中。
 */
@RestController
@RequestMapping("/property/category")
public class PropRepairCategoryController extends BaseController
{
    @Autowired
    private IPropRepairCategoryService categoryService;

    // 分页查询全部报修分类，供后台维护页面展示。
    @PreAuthorize("@ss.hasPermi('property:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(PropRepairCategory category)
    {
        startPage();
        return getDataTable(categoryService.selectCategoryList(category));
    }

    // 只返回正常分类作为工单表单选项，避免用户选择已停用分类。
    @GetMapping("/options")
    public AjaxResult options()
    {
        PropRepairCategory category = new PropRepairCategory();
        category.setStatus("0");
        return success(categoryService.selectCategoryList(category));
    }

    // 查询单个分类详情，为编辑弹窗提供可信的数据库数据。
    @PreAuthorize("@ss.hasPermi('property:category:query')")
    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId) { return success(categoryService.selectCategoryById(categoryId)); }

    // 新增分类并记录创建账号，名称唯一性由服务层保证。
    @PreAuthorize("@ss.hasPermi('property:category:add')")
    @Log(title = "报修分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PropRepairCategory category) { category.setCreateBy(getUsername()); return toAjax(categoryService.insertCategory(category)); }

    // 修改分类名称、排序或状态，并写入更新人信息。
    @PreAuthorize("@ss.hasPermi('property:category:edit')")
    @Log(title = "报修分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PropRepairCategory category) { category.setUpdateBy(getUsername()); return toAjax(categoryService.updateCategory(category)); }

    // 批量删除分类，操作日志用于追踪配置变化。
    @PreAuthorize("@ss.hasPermi('property:category:remove')")
    @Log(title = "报修分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable("categoryIds") Long[] categoryIds) { return toAjax(categoryService.deleteCategoryByIds(categoryIds)); }
}
