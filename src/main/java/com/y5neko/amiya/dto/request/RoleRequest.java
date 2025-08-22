package com.y5neko.amiya.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RoleRequest implements Serializable {

    @NotBlank(message = "角色名不能为空", groups = {Create.class, Update.class})
    private String roleName;

    @NotBlank(message = "角色描述不能为空", groups = {Create.class, Update.class})
    private String description;

    /** 校验组：新增 */
    public interface Create {}

    /** 校验组：更新 */
    public interface Update {}
}
