package com.wuye.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wuye.common.config.WuYeConfig;
import com.wuye.common.core.domain.AjaxResult;
import com.wuye.common.core.domain.entity.SysUser;
import com.wuye.common.utils.SecurityUtils;
import com.wuye.common.utils.StringUtils;
import com.wuye.system.service.ISysUserService;

/**
 * Index and lock-screen endpoints.
 *
 * @author wuye
 */
@RestController
public class SysIndexController
{
    @Autowired
    private WuYeConfig wuyeConfig;

    @Autowired
    private ISysUserService userService;

    /**
     * Root greeting.
     */
    @RequestMapping("/")
    public String index()
    {
        return StringUtils.format(
                "Welcome to {}, current version: v{}. Please access the system through the frontend application.",
                wuyeConfig.getName(), wuyeConfig.getVersion());
    }

    /**
     * Unlock the screen with the current user's password.
     */
    @PostMapping("/unlockscreen")
    public AjaxResult unlockScreen(@RequestBody Map<String, String> body)
    {
        String password = body.get("password");
        if (StringUtils.isEmpty(password))
        {
            return AjaxResult.error("密码不能为空");
        }
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        if (user == null)
        {
            return AjaxResult.error("登录状态已过期，请重新登录");
        }
        if (!SecurityUtils.matchesPassword(password, user.getPassword()))
        {
            return AjaxResult.error("密码错误，请重新输入");
        }

        return AjaxResult.success("Screen unlocked successfully");
    }
}
