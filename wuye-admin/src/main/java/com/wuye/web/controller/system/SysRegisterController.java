package com.wuye.web.controller.system;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.wuye.common.annotation.Anonymous;
import com.wuye.common.core.controller.BaseController;
import com.wuye.common.core.domain.AjaxResult;
import com.wuye.common.core.domain.model.RegisterBody;
import com.wuye.common.utils.StringUtils;
import com.wuye.framework.web.service.SysRegisterService;
import com.wuye.system.domain.PropBuilding;
import com.wuye.system.service.IPropBuildingService;
import com.wuye.system.service.ISysConfigService;

/**
 * 业主自助注册入口。
 *
 * <p>控制器负责检查系统注册开关并提供可选楼栋，具体的账号创建、业主角色授予和
 * 房屋绑定交给注册服务在同一业务流程中完成。这样既保留统一配置开关，也避免前端
 * 分多次请求造成“用户已创建但房屋未绑定”的半成品数据。</p>
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IPropBuildingService buildingService;

    /**
     * 获取注册页面可选的正常楼栋，只暴露编号和名称供匿名页面选择。
     */
    @Anonymous
    @GetMapping("/register/buildings")
    public AjaxResult getRegisterBuildings()
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        PropBuilding query = new PropBuilding();
        query.setStatus("0");
        List<Map<String, Object>> buildings = buildingService.selectBuildingList(query).stream().map(building -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("buildingId", building.getBuildingId());
            item.put("buildingName", building.getBuildingName());
            return item;
        }).toList();
        return success(buildings);
    }

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        // 提交时再次检查开关，不能只依赖页面是否显示注册链接。
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
