package com.wuye.system.service;

import java.util.List;
import com.baomidou.mybatisplus.spring.service.IService;
import com.wuye.common.core.domain.entity.SysUser;
import com.wuye.system.domain.PropRoom;

/** 房屋业务契约，包含后台建房和业主自助注册绑定两类入口。 */
public interface IPropRoomService extends IService<PropRoom>
{
    List<PropRoom> selectRoomList(PropRoom room);
    PropRoom selectRoomById(Long roomId);
    int insertRoom(PropRoom room);
    int registerOwnerAndRoom(PropRoom room, SysUser owner);
    int updateRoom(PropRoom room);
    int deleteRoomByIds(Long[] roomIds);
}
