package com.wuye.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.wuye.common.exception.ServiceException;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.common.core.domain.entity.SysDept;
import com.wuye.common.core.domain.entity.SysRole;
import com.wuye.common.core.domain.entity.SysUser;
import com.wuye.system.domain.PropBuilding;
import com.wuye.system.domain.PropRoom;
import com.wuye.system.mapper.PropRoomMapper;
import com.wuye.system.mapper.SysDeptMapper;
import com.wuye.system.mapper.SysRoleMapper;
import com.wuye.system.service.IPropBuildingService;
import com.wuye.system.service.IPropRoomService;
import com.wuye.system.service.ISysConfigService;
import com.wuye.system.service.ISysUserService;

/**
 * 房屋与业主账号业务服务。
 *
 * 负责校验房屋唯一性、创建或复用业主账号、补齐业主角色和部门，并维护账号资料同步。
 * 房屋和用户写入使用同一事务，可避免只创建账号却未绑定房屋的半成品数据。
 */
@Service
public class PropRoomServiceImpl extends ServiceImpl<PropRoomMapper, PropRoom> implements IPropRoomService
{
    @Autowired
    private PropRoomMapper roomMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private IPropBuildingService buildingService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    // 按楼栋、房号和业主等条件查询房屋绑定信息。
    public List<PropRoom> selectRoomList(PropRoom room) { return roomMapper.selectRoomList(room); }
    // 根据主键读取房屋及关联业主资料，供详情和工单校验使用。
    public PropRoom selectRoomById(Long roomId) { return roomMapper.selectRoomById(roomId); }

    // 在同一事务中创建或复用业主账号并新增房屋，防止绑定关系不完整。
    @Transactional
    public int insertRoom(PropRoom room)
    {
        validateRoom(room, null);
        // 先创建或复用业主用户，再把用户主键写入房屋，建立稳定的账号关联。
        room.setOwnerId(createOwnerUser(room));
        return roomMapper.insertRoom(room);
    }

    /**
     * 自助注册时在同一事务中创建业主账号并绑定房屋。
     */
    @Transactional
    public int registerOwnerAndRoom(PropRoom room, SysUser owner)
    {
        if (owner == null)
        {
            throw new ServiceException("注册用户信息不能为空");
        }
        room.setLoginAccount(owner.getUserName());
        validateRoom(room, null);
        owner.setDeptId(resolveOwnerDeptId());
        owner.setRoleIds(new Long[]{resolveOwnerRoleId()});
        owner.setPhonenumber(room.getOwnerPhone());
        owner.setNickName(room.getOwnerName());
        owner.setStatus("0");
        if (!userService.checkPhoneUnique(owner))
        {
            throw new ServiceException("该手机号码已绑定账号，请直接登录；如无法登录，请主动联系物业管理员处理");
        }
        if (userService.insertUser(owner) <= 0)
        {
            throw new ServiceException("业主账号创建失败，请稍后重试");
        }
        room.setOwnerId(owner.getUserId());
        // 账号与房屋必须同时成功；任一步异常都会由事务整体回滚。
        room.setStatus("0");
        int rows = roomMapper.insertRoom(room);
        if (rows <= 0)
        {
            throw new ServiceException("房屋绑定失败，请稍后重试");
        }
        return rows;
    }

    // 修改房屋时同步业主昵称和手机号，账号变化则重新执行绑定规则。
    @Transactional
    public int updateRoom(PropRoom room)
    {
        if (room == null || room.getRoomId() == null)
        {
            throw new ServiceException("房屋信息不存在");
        }
        PropRoom existing = roomMapper.selectRoomById(room.getRoomId());
        if (existing == null)
        {
            throw new ServiceException("房屋信息不存在");
        }
        validateRoom(room, room.getRoomId());
        if (existing.getOwnerId() != null
                && normalizeText(existing.getLoginAccount()).equals(room.getLoginAccount()))
        {
            // 登录账号未变化时更新原业主资料，避免重复创建用户。
            SysUser owner = userService.selectUserById(existing.getOwnerId());
            if (owner != null)
            {
                owner.setNickName(room.getOwnerName());
                owner.setPhonenumber(room.getOwnerPhone());
                if (!userService.checkPhoneUnique(owner))
                {
                    throw new ServiceException("手机号码已被其他用户使用");
                }
                userService.updateUserProfile(owner);
                ensureOwnerRole(owner);
            }
            room.setOwnerId(existing.getOwnerId());
        }
        else
        {
            // 登录账号改变时重新执行账号复用/创建规则，并更新房屋绑定关系。
            room.setOwnerId(createOwnerUser(room));
        }
        return roomMapper.updateRoom(room);
    }

    // 批量删除房屋记录，减少逐条数据库操作带来的不一致风险。
    public int deleteRoomByIds(Long[] roomIds) { return roomMapper.deleteRoomByIds(roomIds); }

    private Long createOwnerUser(PropRoom room)
    {
        // 优先复用账号和手机号匹配的用户，否则按系统初始密码创建新业主。
        String userName = room.getLoginAccount();
        String ownerPhone = room.getOwnerPhone();
        SysUser existingUser = userService.selectUserByUserName(userName);
        if (existingUser != null)
        {
            // 允许复用手机号一致的普通账号，但禁止把超级管理员误绑定成业主。
            if (existingUser.isAdmin())
            {
                throw new ServiceException("超级管理员账号不能绑定为业主，请更换登录账号");
            }
            String existingPhone = existingUser.getPhonenumber() == null ? "" : existingUser.getPhonenumber().trim();
            if (!ownerPhone.equals(existingPhone))
            {
                throw new ServiceException("登录账号已存在且手机号不一致，请更换账号或核对手机号");
            }
            existingUser.setNickName(room.getOwnerName());
            existingUser.setPhonenumber(ownerPhone);
            userService.updateUserProfile(existingUser);
            ensureOwnerRole(existingUser);
            return existingUser.getUserId();
        }

        SysUser user = new SysUser();
        user.setUserName(userName);
        user.setNickName(room.getOwnerName());
        user.setPhonenumber(ownerPhone);
        if (!userService.checkPhoneUnique(user))
        {
            throw new ServiceException("手机号码已被其他用户使用，请核对业主信息");
        }
        String initialPassword = configService.selectConfigByKey("sys.user.initPassword");
        // 初始密码来自系统参数，便于管理员统一调整，不在业务代码中写死密码。
        if (initialPassword == null || initialPassword.trim().isEmpty())
        {
            throw new ServiceException("系统初始密码未配置，暂时无法创建业主账号");
        }
        user.setPassword(SecurityUtils.encryptPassword(initialPassword));
        user.setDeptId(resolveOwnerDeptId());
        user.setStatus("0");
        user.setCreateBy(room.getCreateBy());
        userService.insertUser(user);
        userService.insertUserAuth(user.getUserId(), new Long[]{resolveOwnerRoleId()});
        return user.getUserId();
    }

    private void ensureOwnerRole(SysUser user)
    {
        // 在保留已有角色的基础上补齐业主角色，避免覆盖其他合法授权。
        Long ownerRoleId = resolveOwnerRoleId();
        List<Long> roleIds = new ArrayList<>();
        boolean hasOwnerRole = false;
        if (user.getRoles() != null)
        {
            for (SysRole role : user.getRoles())
            {
                roleIds.add(role.getRoleId());
                if (ownerRoleId.equals(role.getRoleId()))
                {
                    hasOwnerRole = true;
                }
            }
        }
        if (!hasOwnerRole)
        {
            // 保留用户原有角色并追加业主角色，避免覆盖其已有授权。
            roleIds.add(ownerRoleId);
            userService.insertUserAuth(user.getUserId(), roleIds.toArray(new Long[0]));
        }
    }

    private Long resolveOwnerRoleId()
    {
        // 通过稳定的角色标识查询主键，避免把环境相关数据库编号写死在代码中。
        SysRole query = new SysRole();
        query.setRoleKey("property_owner");
        query.setStatus("0");
        for (SysRole role : roleMapper.selectRoleList(query))
        {
            if ("property_owner".equals(role.getRoleKey()))
            {
                return role.getRoleId();
            }
        }
        throw new ServiceException("系统未配置可用的业主角色");
    }

    private Long resolveOwnerDeptId()
    {
        // 按业务部门名称解析归属，保证自动创建的业主进入统一组织节点。
        SysDept query = new SysDept();
        query.setDeptName("业主服务组");
        query.setStatus("0");
        for (SysDept dept : deptMapper.selectDeptList(query))
        {
            if ("业主服务组".equals(dept.getDeptName()))
            {
                return dept.getDeptId();
            }
        }
        throw new ServiceException("系统未配置可用的业主服务组");
    }

    private void validateRoom(PropRoom room, Long currentRoomId)
    {
        // 同一楼栋、单元和房号必须唯一，这是注册和报修正确关联房屋的基础。
        if (room == null)
        {
            throw new ServiceException("房屋信息不能为空");
        }
        if (room.getBuildingId() == null)
        {
            throw new ServiceException("请选择楼栋");
        }
        PropBuilding building = buildingService.selectBuildingById(room.getBuildingId());
        if (building == null || !"0".equals(building.getStatus()))
        {
            throw new ServiceException("所选楼栋不存在或已停用");
        }
        room.setUnitNo(normalizeText(room.getUnitNo()));
        room.setRoomNo(requireText(room.getRoomNo(), "请填写房号", 30));
        room.setOwnerName(requireText(room.getOwnerName(), "请填写业主姓名", 20));
        if (room.getOwnerName().length() < 2)
        {
            throw new ServiceException("业主姓名长度不能少于2个字符");
        }
        room.setLoginAccount(requireText(room.getLoginAccount(), "请填写登录账号", 20));
        if (room.getLoginAccount().length() < 2)
        {
            throw new ServiceException("登录账号长度不能少于2个字符");
        }
        room.setOwnerPhone(requireText(room.getOwnerPhone(), "请填写手机号码", 11));
        if (!room.getOwnerPhone().matches("^1[3-9]\\d{9}$"))
        {
            throw new ServiceException("请输入正确的11位手机号码");
        }
        if (room.getUnitNo().length() > 30)
        {
            throw new ServiceException("单元长度不能超过30个字符");
        }
        if (!"0".equals(room.getStatus()) && !"1".equals(room.getStatus()))
        {
            room.setStatus("0");
        }
        PropRoom query = new PropRoom();
        query.setBuildingId(room.getBuildingId());
        query.setRoomNo(room.getRoomNo());
        for (PropRoom existing : roomMapper.selectRoomList(query))
        {
            if ((currentRoomId == null || !currentRoomId.equals(existing.getRoomId()))
                    && room.getRoomNo().equals(existing.getRoomNo())
                    && room.getUnitNo().equals(normalizeText(existing.getUnitNo())))
            {
                throw new ServiceException("该房屋信息已注册，请直接登录；如无法登录，请主动联系物业管理员处理");
            }
        }
    }

    private String requireText(String value, String message, int maxLength)
    {
        // 对必填文本统一执行去空格、非空和长度校验，减少重复分支。
        String text = normalizeText(value);
        if (text.isEmpty())
        {
            throw new ServiceException(message);
        }
        if (text.length() > maxLength)
        {
            throw new ServiceException(message.replace("请填写", "") + "长度不能超过" + maxLength + "个字符");
        }
        return text;
    }

    private String normalizeText(String value)
    {
        // 将空值归一为空字符串，便于比较和校验逻辑避免空指针。
        return value == null ? "" : value.trim();
    }

}
