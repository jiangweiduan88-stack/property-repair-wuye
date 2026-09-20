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
import com.wuye.system.domain.PropBuilding;
import com.wuye.system.service.IPropBuildingService;

/**
 * 楼栋信息接口。
 *
 * 负责分页查询、详情和增删改入口，并在控制器层校验菜单权限、记录操作日志。
 * 这样可以在业务服务执行前拦截越权请求，同时保留完整的后台审计记录。
 */
@RestController
@RequestMapping("/property/building")
public class PropBuildingController extends BaseController
{
    @Autowired
    private IPropBuildingService buildingService;

    // 分页查询楼栋资料，统一返回前端表格所需的 rows 和 total。
    @PreAuthorize("@ss.hasPermi('property:building:list')")
    @GetMapping("/list")
    public TableDataInfo list(PropBuilding building)
    {
        startPage();
        List<PropBuilding> list = buildingService.selectBuildingList(building);
        return getDataTable(list);
    }

    // 按主键获取楼栋详情，供修改表单回显和房屋关联校验使用。
    @PreAuthorize("@ss.hasPermi('property:building:query')")
    @GetMapping("/{buildingId}")
    public AjaxResult getInfo(@PathVariable("buildingId") Long buildingId) { return success(buildingService.selectBuildingById(buildingId)); }

    // 新增楼栋时记录当前操作账号，便于后续审计数据来源。
    @PreAuthorize("@ss.hasPermi('property:building:add')")
    @Log(title = "楼栋管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PropBuilding building) { building.setCreateBy(getUsername()); return toAjax(buildingService.insertBuilding(building)); }

    // 更新楼栋并记录修改人，具体字段规则由服务层统一校验。
    @PreAuthorize("@ss.hasPermi('property:building:edit')")
    @Log(title = "楼栋管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PropBuilding building) { building.setUpdateBy(getUsername()); return toAjax(buildingService.updateBuilding(building)); }

    // 批量删除选中楼栋，权限和操作日志在进入服务前统一处理。
    @PreAuthorize("@ss.hasPermi('property:building:remove')")
    @Log(title = "楼栋管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{buildingIds}")
    public AjaxResult remove(@PathVariable("buildingIds") Long[] buildingIds) { return toAjax(buildingService.deleteBuildingByIds(buildingIds)); }
}
