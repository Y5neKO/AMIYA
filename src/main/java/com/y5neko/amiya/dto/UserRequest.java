package com.y5neko.amiya.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UserRequest implements Serializable {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空", groups = Create.class) // 更新时密码可为空
    private String password;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /** 是否启用 */
    @NotNull(message = "启用状态不能为空")
    private Boolean isActive;

    /** 用于区分新增/更新的校验组 */
    public interface Create {}
    public interface Update {}
}
