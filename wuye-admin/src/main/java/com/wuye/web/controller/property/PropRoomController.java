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
import com.wuye.common.utils.SecurityUtils;
import com.wuye.system.domain.PropRoom;
import com.wuye.system.service.IPropRoomService;

/**
 * 房屋及业主绑定接口。
 *
 * 管理楼栋下的房屋资料，并为报修表单提供可选房屋。普通业主只能取得本人房屋，
 * 管理人员可以取得全部房屋，从接口源头减少前端越权展示和伪造请求风险。
 */
@RestController
@RequestMapping("/property/room")
public class PropRoomController extends BaseController
{
    @Autowired
    private IPropRoomService roomService;

    // 分页查询房屋及业主绑定关系，供物业后台统一维护。
    @PreAuthorize("@ss.hasPermi('property:room:list')")
    @GetMapping("/list")
    public TableDataInfo list(PropRoom room)
    {
        startPage();
        List<PropRoom> list = roomService.selectRoomList(room);
        return getDataTable(list);
    }

    // 根据登录身份返回可报修房屋选项，普通业主只能选择本人房屋。
    @GetMapping("/options")
    public AjaxResult options()
    {
        PropRoom room = new PropRoom();
        room.setStatus("0");
        // 没有房屋管理权限的账号只能选择本人名下的正常房屋，保证报修归属准确。
        if (!SecurityUtils.hasPermi("property:room:list"))
        {
            room.setOwnerId(getUserId());
        }
        return success(roomService.selectRoomList(room));
    }

    // 查询房屋详情，用于编辑回显和绑定信息核对。
    @PreAuthorize("@ss.hasPermi('property:room:query')")
    @GetMapping("/{roomId}")
    public AjaxResult getInfo(@PathVariable("roomId") Long roomId) { return success(roomService.selectRoomById(roomId)); }

    // 新增房屋并记录创建账号，服务层同步创建或复用业主用户。
    @PreAuthorize("@ss.hasPermi('property:room:add')")
    @Log(title = "房屋管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PropRoom room) { room.setCreateBy(getUsername()); return toAjax(roomService.insertRoom(room)); }

    // 更新房屋与业主资料，并保留修改账号用于审计。
    @PreAuthorize("@ss.hasPermi('property:room:edit')")
    @Log(title = "房屋管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PropRoom room) { room.setUpdateBy(getUsername()); return toAjax(roomService.updateRoom(room)); }

    // 批量删除房屋记录，避免前端逐条请求造成部分成功。
    @PreAuthorize("@ss.hasPermi('property:room:remove')")
    @Log(title = "房屋管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roomIds}")
    public AjaxResult remove(@PathVariable("roomIds") Long[] roomIds) { return toAjax(roomService.deleteRoomByIds(roomIds)); }
}
