package com.y5neko.amiya.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserRequest implements Serializable {

    @NotBlank(message = "用户名不能为空", groups = {Create.class, Update.class})
    private String username;

    @NotBlank(message = "密码不能为空", groups = Create.class) // 更新时密码可为空
    private String password;

    @NotBlank(message = "邮箱不能为空", groups = {Create.class, Update.class})
    @Email(message = "邮箱格式不正确", groups = {Create.class, Update.class})
    private String email;

    @NotNull(message = "角色ID不能为空", groups = {Create.class, Update.class})
    private Long roleId;

    /** 是否启用 */
    @NotNull(message = "启用状态不能为空", groups = {Create.class, Update.class})
    private Boolean isActive;

    /** 用于区分新增/更新的校验组 */
    public interface Create {}
    public interface Update {}
}
