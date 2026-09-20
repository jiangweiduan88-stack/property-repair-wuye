package com.wuye.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.wuye.common.constant.CacheConstants;
import com.wuye.common.constant.Constants;
import com.wuye.common.constant.UserConstants;
import com.wuye.common.core.domain.entity.SysUser;
import com.wuye.common.core.domain.model.RegisterBody;
import com.wuye.common.core.redis.RedisCache;
import com.wuye.common.exception.user.CaptchaException;
import com.wuye.common.exception.user.CaptchaExpireException;
import com.wuye.common.utils.DateUtils;
import com.wuye.common.utils.MessageUtils;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.common.utils.StringUtils;
import com.wuye.framework.manager.AsyncManager;
import com.wuye.framework.manager.factory.AsyncFactory;
import com.wuye.system.domain.PropRoom;
import com.wuye.system.service.IPropRoomService;
import com.wuye.system.service.ISysConfigService;
import com.wuye.system.service.ISysUserService;

/**
 * 业主注册校验与账号、房屋绑定编排服务。
 *
 * <p>本服务先完成验证码和输入合法性校验，再把可信数据交给房屋服务创建用户、授予
 * 业主角色并绑定房屋。核心写入集中在事务服务中，可保证注册失败时不会遗留孤立账号。</p>
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IPropRoomService roomService;

    /**
     * 完成业主自助注册。
     *
     * @return 空字符串表示成功，其他内容为可直接展示的业务错误原因
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (StringUtils.isEmpty(registerBody.getOwnerName()))
        {
            msg = "业主姓名不能为空";
        }
        else if (StringUtils.isEmpty(registerBody.getPhonenumber())
                || !registerBody.getPhonenumber().trim().matches("^1[3-9]\\d{9}$"))
        {
            msg = "请输入正确的11位手机号码";
        }
        else if (registerBody.getBuildingId() == null)
        {
            msg = "请选择楼栋";
        }
        else if (StringUtils.isEmpty(registerBody.getRoomNo()))
        {
            msg = "房号不能为空";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            // 密码只保存不可逆散列，注册人同时作为本次数据的审计创建者。
            sysUser.setPwdUpdateDate(DateUtils.getNowDate());
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            sysUser.setCreateBy(username);

            // 将页面中的地址和业主资料转换为房屋领域对象，由房屋服务原子化落库。
            PropRoom room = new PropRoom();
            room.setBuildingId(registerBody.getBuildingId());
            room.setUnitNo(registerBody.getUnitNo());
            room.setRoomNo(registerBody.getRoomNo());
            room.setOwnerName(registerBody.getOwnerName());
            room.setOwnerPhone(registerBody.getPhonenumber());
            room.setCreateBy(username);
            roomService.registerOwnerAndRoom(room, sysUser);
            // 审计日志异步记录，避免日志写入延长用户注册接口的响应时间。
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER,
                    MessageUtils.message("user.register.success")));
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }
}
