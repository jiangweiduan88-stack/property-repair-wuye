package com.wuye.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.wuye.common.annotation.DataScope;
import com.wuye.common.constant.UserConstants;
import com.wuye.common.core.domain.entity.SysDept;
import com.wuye.common.core.domain.entity.SysRole;
import com.wuye.common.core.domain.entity.SysUser;
import com.wuye.common.exception.ServiceException;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.common.utils.StringUtils;
import com.wuye.common.utils.spring.SpringUtils;
import com.wuye.system.domain.SysPost;
import com.wuye.system.domain.SysUserImportError;
import com.wuye.system.domain.SysUserImportResult;
import com.wuye.system.domain.SysUserImportRow;
import com.wuye.system.domain.SysUserPost;
import com.wuye.system.domain.SysUserRole;
import com.wuye.system.mapper.SysPostMapper;
import com.wuye.system.mapper.SysRoleMapper;
import com.wuye.system.mapper.SysUserMapper;
import com.wuye.system.mapper.SysUserPostMapper;
import com.wuye.system.mapper.SysUserRoleMapper;
import com.wuye.system.service.ISysConfigService;
import com.wuye.system.service.ISysDeptService;
import com.wuye.system.service.ISysUserService;

/**
 * 用户 业务层处理
 * 
 * @author wuye
 */
@Service
public class SysUserServiceImpl implements ISysUserService
{
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 根据条件分页查询已分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectAllocatedList(SysUser user)
    {
        return userMapper.selectAllocatedList(user);
    }

    /**
     * 根据条件分页查询未分配用户角色列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUnallocatedList(SysUser user)
    {
        return userMapper.selectUnallocatedList(user);
    }

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysRole::getRoleName).collect(Collectors.joining(","));
    }

    /**
     * 查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName)
    {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        if (CollectionUtils.isEmpty(list))
        {
            return StringUtils.EMPTY;
        }
        return list.stream().map(SysPost::getPostName).collect(Collectors.joining(","));
    }

    /**
     * 校验用户名称是否唯一
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean checkUserNameUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkUserNameUnique(user.getUserName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public boolean checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员用户");
        }
    }

    /**
     * 校验用户是否有数据权限
     * 
     * @param userId 用户id
     */
    @Override
    public void checkUserDataScope(Long userId)
    {
        if (!SecurityUtils.isAdmin())
        {
            SysUser user = new SysUser();
            user.setUserId(userId);
            List<SysUser> users = SpringUtils.getAopProxy(this).selectUserList(user);
            if (StringUtils.isEmpty(users))
            {
                throw new ServiceException("没有权限访问用户数据！");
            }
        }
    }

    /**
     * 新增保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user)
    {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 注册用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public boolean registerUser(SysUser user)
    {
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 修改保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateUser(user);
    }

    /**
     * 用户授权角色
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    @Override
    @Transactional
    public void insertUserAuth(Long userId, Long[] roleIds)
    {
        userRoleMapper.deleteUserRoleByUserId(userId);
        insertUserRole(userId, roleIds);
    }

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user)
    {
        return userMapper.updateUserStatus(user.getUserId(), user.getStatus());
    }

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     * 
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(Long userId, String avatar)
    {
        return userMapper.updateUserAvatar(userId, avatar) > 0;
    }

    /**
     * 更新用户登录信息（IP和登录时间）
     * 
     * @param userId 用户ID
     * @param loginIp 登录IP地址
     * @param loginDate 登录时间
     * @return 结果
     */
    public void updateLoginInfo(Long userId, String loginIp, Date loginDate)
    {
        userMapper.updateLoginInfo(userId, loginIp, loginDate);
    }

    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user)
    {
        return userMapper.resetUserPwd(user.getUserId(), user.getPassword());
    }

    /**
     * 重置用户密码
     * 
     * @param userId 用户ID
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(Long userId, String password)
    {
        return userMapper.resetUserPwd(userId, password);
    }

    /**
     * 新增用户角色信息
     * 
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        this.insertUserRole(user.getUserId(), user.getRoleIds());
    }

    /**
     * 新增用户岗位信息
     * 
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user)
    {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotEmpty(posts))
        {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>(posts.length);
            for (Long postId : posts)
            {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            userPostMapper.batchUserPost(list);
        }
    }

    /**
     * 新增用户角色信息
     * 
     * @param userId 用户ID
     * @param roleIds 角色组
     */
    public void insertUserRole(Long userId, Long[] roleIds)
    {
        if (StringUtils.isNotEmpty(roleIds))
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>(roleIds.length);
            for (Long roleId : roleIds)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                list.add(ur);
            }
            userRoleMapper.batchUserRole(list);
        }
    }

    /**
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId)
    {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds)
    {
        for (Long userId : userIds)
        {
            checkUserAllowed(new SysUser(userId));
            checkUserDataScope(userId);
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     * 
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUserImportResult importUser(List<SysUserImportRow> userList, Boolean isUpdateSupport, String operName,
            Boolean validateOnly)
    {
        SysUserImportResult result = new SysUserImportResult();
        result.setTotal(StringUtils.isNull(userList) ? 0 : userList.size());
        if (StringUtils.isNull(userList) || userList.size() == 0)
        {
            addImportError(result, 2, "A", "用户昵称", "", "导入文件没有可校验的用户数据");
            return validationFailed(result);
        }

        Map<String, SysDept> departments = new HashMap<String, SysDept>();
        for (SysDept dept : deptService.selectDeptList(new SysDept()))
        {
            if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()))
            {
                departments.putIfAbsent(clean(dept.getDeptName()), dept);
            }
        }
        Map<String, SysPost> posts = new HashMap<String, SysPost>();
        for (SysPost post : postMapper.selectPostAll())
        {
            if (UserConstants.NORMAL.equals(post.getStatus()))
            {
                posts.putIfAbsent(clean(post.getPostName()), post);
            }
        }
        Map<String, SysRole> roles = new HashMap<String, SysRole>();
        for (SysRole role : roleMapper.selectRoleAll())
        {
            if (UserConstants.ROLE_NORMAL.equals(role.getStatus()) && !role.isAdmin())
            {
                roles.putIfAbsent(clean(role.getRoleName()), role);
            }
        }

        Map<String, Integer> firstUserNameRows = new HashMap<String, Integer>();
        Map<String, Integer> firstPhoneRows = new HashMap<String, Integer>();
        List<SysUser> preparedUsers = new ArrayList<SysUser>();

        for (int index = 0; index < userList.size(); index++)
        {
            SysUserImportRow importRow = userList.get(index);
            int rowNum = importRow.getImportRowNum() == null ? index + 2 : importRow.getImportRowNum();
            int errorCountBefore = result.getErrors().size();
            String nickName = clean(importRow.getNickName());
            String userName = clean(importRow.getUserName());
            String sexText = clean(importRow.getSex());
            String phone = clean(importRow.getPhonenumber());
            String deptName = clean(importRow.getDeptName());
            String statusText = clean(importRow.getStatus());
            String postNames = clean(importRow.getPostNames());
            String roleNames = clean(importRow.getRoleNames());

            if (StringUtils.isEmpty(nickName))
            {
                addImportError(result, rowNum, "A", "用户昵称", nickName, "用户昵称不能为空");
            }
            else if (nickName.length() > 30)
            {
                addImportError(result, rowNum, "A", "用户昵称", nickName, "用户昵称长度不能超过30个字符");
            }
            if (StringUtils.isEmpty(userName))
            {
                addImportError(result, rowNum, "B", "用户名称", userName, "用户名称不能为空");
            }
            else if (userName.length() < UserConstants.USERNAME_MIN_LENGTH
                    || userName.length() > UserConstants.USERNAME_MAX_LENGTH)
            {
                addImportError(result, rowNum, "B", "用户名称", userName, "用户名称长度必须介于2和20个字符之间");
            }
            if (!"男".equals(sexText) && !"女".equals(sexText))
            {
                addImportError(result, rowNum, "C", "用户性别", sexText, "用户性别为必填项，只能填写男或女");
            }
            if (!phone.matches("\\d{11}"))
            {
                addImportError(result, rowNum, "D", "手机号码", phone, "手机号码为必填项，必须是11位数字");
            }
            if (StringUtils.isNotEmpty(statusText) && !"正常".equals(statusText) && !"停用".equals(statusText))
            {
                addImportError(result, rowNum, "F", "账号状态", statusText, "账号状态只能填写正常或停用");
            }

            SysUser existingUser = StringUtils.isEmpty(userName) ? null : userMapper.selectUserByUserName(userName);
            Integer firstUserNameRow = firstUserNameRows.putIfAbsent(userName, rowNum);
            if (StringUtils.isNotEmpty(userName) && firstUserNameRow != null)
            {
                addImportError(result, rowNum, "B", "用户名称", userName,
                        "用户名称在导入文件中重复，首次出现于第" + firstUserNameRow + "行");
            }
            Integer firstPhoneRow = firstPhoneRows.putIfAbsent(phone, rowNum);
            if (phone.matches("\\d{11}") && firstPhoneRow != null)
            {
                addImportError(result, rowNum, "D", "手机号码", phone,
                        "手机号码在导入文件中重复，首次出现于第" + firstPhoneRow + "行");
            }
            if (existingUser != null && !Boolean.TRUE.equals(isUpdateSupport))
            {
                addImportError(result, rowNum, "B", "用户名称", userName, "用户名称已存在；如需覆盖请勾选更新已有用户");
            }
            else if (existingUser != null && existingUser.isAdmin())
            {
                addImportError(result, rowNum, "B", "用户名称", userName, "不允许通过导入修改超级管理员");
            }
            if (phone.matches("\\d{11}"))
            {
                SysUser phoneOwner = userMapper.checkPhoneUnique(phone);
                if (phoneOwner != null && (existingUser == null
                        || !phoneOwner.getUserId().equals(existingUser.getUserId())))
                {
                    addImportError(result, rowNum, "D", "手机号码", phone, "手机号码已被其他用户使用");
                }
            }

            SysDept dept = null;
            if (StringUtils.isNotEmpty(deptName))
            {
                dept = departments.get(deptName);
                if (dept == null)
                {
                    addImportError(result, rowNum, "E", "归属部门", deptName, "归属部门不存在、已停用或无权使用");
                }
            }

            Long[] postIds = resolvePostIds(postNames, posts, result, rowNum);
            Long[] roleIds = resolveRoleIds(roleNames, roles, result, rowNum);
            if (result.getErrors().size() == errorCountBefore)
            {
                SysUser user = new SysUser();
                user.setUserId(existingUser == null ? null : existingUser.getUserId());
                user.setUserName(userName);
                user.setNickName(nickName);
                user.setSex("男".equals(sexText) ? "0" : "1");
                user.setPhonenumber(phone);
                user.setDeptId(dept == null && existingUser != null ? existingUser.getDeptId()
                        : dept == null ? null : dept.getDeptId());
                user.setStatus(StringUtils.isEmpty(statusText) && existingUser != null ? existingUser.getStatus()
                        : "停用".equals(statusText) ? "1" : "0");
                user.setPostIds(StringUtils.isEmpty(postNames) && existingUser != null
                        ? postMapper.selectPostListByUserId(existingUser.getUserId()).toArray(new Long[0]) : postIds);
                user.setRoleIds(StringUtils.isEmpty(roleNames) && existingUser != null
                        ? roleMapper.selectRoleListByUserId(existingUser.getUserId()).toArray(new Long[0]) : roleIds);
                preparedUsers.add(user);
            }
        }

        result.setValidCount(preparedUsers.size());
        result.setInvalidCount(Math.max(0, result.getTotal() - preparedUsers.size()));
        result.setPassed(result.getInvalidCount() == 0);
        if (Boolean.TRUE.equals(validateOnly))
        {
            result.setMessage("校验完成：通过 " + result.getValidCount() + " 条，未通过 "
                    + result.getInvalidCount() + " 条");
            return result;
        }

        for (SysUser user : preparedUsers)
        {
            if (user.getUserId() != null)
            {
                checkUserAllowed(user);
                checkUserDataScope(user.getUserId());
            }
            if (user.getDeptId() != null)
            {
                deptService.checkDeptDataScope(user.getDeptId());
            }
        }

        if (preparedUsers.isEmpty())
        {
            result.setSuccessCount(0);
            result.setMessage("没有校验通过的数据，未导入任何用户");
            return result;
        }

        String initialPassword = SecurityUtils.encryptPassword(configService.selectConfigByKey("sys.user.initPassword"));
        for (SysUser user : preparedUsers)
        {
            if (user.getUserId() == null)
            {
                user.setPassword(initialPassword);
                user.setCreateBy(operName);
                userMapper.insertUser(user);
                insertUserPost(user);
                insertUserRole(user);
            }
            else
            {
                user.setUpdateBy(operName);
                updateUser(user);
            }
        }
        result.setSuccessCount(preparedUsers.size());
        result.setMessage("导入完成：成功导入 " + preparedUsers.size() + " 条，跳过 "
                + result.getInvalidCount() + " 条未通过数据");
        return result;
    }

    private Long[] resolvePostIds(String postNames, Map<String, SysPost> posts, SysUserImportResult result, int rowNum)
    {
        List<Long> ids = new ArrayList<Long>();
        for (String postName : splitNames(postNames))
        {
            SysPost post = posts.get(postName);
            if (post == null)
            {
                addImportError(result, rowNum, "G", "岗位", postName, "岗位不存在或已停用");
            }
            else
            {
                ids.add(post.getPostId());
            }
        }
        return ids.toArray(new Long[0]);
    }

    private Long[] resolveRoleIds(String roleNames, Map<String, SysRole> roles, SysUserImportResult result, int rowNum)
    {
        List<Long> ids = new ArrayList<Long>();
        for (String roleName : splitNames(roleNames))
        {
            SysRole role = roles.get(roleName);
            if (role == null)
            {
                addImportError(result, rowNum, "H", "角色", roleName, "角色不存在、已停用或不可分配");
            }
            else
            {
                ids.add(role.getRoleId());
            }
        }
        return ids.toArray(new Long[0]);
    }

    private Set<String> splitNames(String value)
    {
        Set<String> names = new LinkedHashSet<String>();
        if (StringUtils.isEmpty(value))
        {
            return names;
        }
        for (String item : value.replace('，', ',').replace('、', ',').split(","))
        {
            String name = clean(item);
            if (StringUtils.isNotEmpty(name))
            {
                names.add(name);
            }
        }
        return names;
    }

    private String clean(String value)
    {
        return value == null ? "" : value.trim();
    }

    private void addImportError(SysUserImportResult result, int row, String columnCode, String column,
            String value, String message)
    {
        result.addError(new SysUserImportError(row, column, columnCode + row, value, message));
    }

    private SysUserImportResult validationFailed(SysUserImportResult result)
    {
        result.setPassed(false);
        result.setValidCount(0);
        result.setInvalidCount(result.getTotal());
        result.setSuccessCount(0);
        result.setMessage("校验完成：通过 0 条，未通过 " + result.getInvalidCount() + " 条");
        return result;
    }
}
