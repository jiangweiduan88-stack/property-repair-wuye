package com.wuye.system.mapper;

import java.util.List;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuye.system.domain.PropRoom;

/** 房屋持久化接口，负责房屋及其楼栋、业主关联数据的读写。 */
public interface PropRoomMapper extends BaseMapper<PropRoom>
{
    List<PropRoom> selectRoomList(PropRoom room);
    PropRoom selectRoomById(Long roomId);
    int insertRoom(PropRoom room);
    int updateRoom(PropRoom room);
    int deleteRoomByIds(Long[] roomIds);
}
