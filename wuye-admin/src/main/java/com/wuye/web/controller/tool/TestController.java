package com.wuye.web.controller.tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.wuye.common.core.controller.BaseController;
import com.wuye.common.core.domain.R;
import com.wuye.common.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Swagger sample controller.
 *
 * @author wuye
 */
@Tag(name = "User Test API")
@RestController
@RequestMapping("/test/user")
public class TestController extends BaseController
{
    private static final Map<Integer, UserEntity> USERS = new LinkedHashMap<Integer, UserEntity>();

    static
    {
        USERS.put(1, new UserEntity(1, "admin", "admin123", "15888888888"));
        USERS.put(2, new UserEntity(2, "ry", "admin123", "15666666666"));
    }

    @Operation(summary = "Get user list")
    @GetMapping("/list")
    public R<List<UserEntity>> userList()
    {
        return R.ok(new ArrayList<UserEntity>(USERS.values()));
    }

    @Operation(summary = "Get user detail")
    @GetMapping("/{userId}")
    public R<UserEntity> getUser(@PathVariable(name = "userId") Integer userId)
    {
        if (!USERS.isEmpty() && USERS.containsKey(userId))
        {
            return R.ok(USERS.get(userId));
        }
        return R.fail("User not found");
    }

    @Operation(summary = "Create user")
    @PostMapping("/save")
    public R<String> save(UserEntity user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
        {
            return R.fail("User ID cannot be empty");
        }
        USERS.put(user.getUserId(), user);
        return R.ok();
    }

    @Operation(summary = "Update user")
    @PutMapping("/update")
    public R<String> update(@RequestBody UserEntity user)
    {
        if (StringUtils.isNull(user) || StringUtils.isNull(user.getUserId()))
        {
            return R.fail("User ID cannot be empty");
        }
        if (USERS.isEmpty() || !USERS.containsKey(user.getUserId()))
        {
            return R.fail("User not found");
        }
        USERS.remove(user.getUserId());
        USERS.put(user.getUserId(), user);
        return R.ok();
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{userId}")
    public R<String> delete(@PathVariable(name = "userId") Integer userId)
    {
        if (!USERS.isEmpty() && USERS.containsKey(userId))
        {
            USERS.remove(userId);
            return R.ok();
        }
        return R.fail("User not found");
    }
}

@Schema(description = "User entity")
class UserEntity
{
    @Schema(title = "User ID")
    private Integer userId;

    @Schema(title = "Username")
    private String username;

    @Schema(title = "Password")
    private String password;

    @Schema(title = "Mobile")
    private String mobile;

    public UserEntity()
    {
    }

    public UserEntity(Integer userId, String username, String password, String mobile)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }
}
