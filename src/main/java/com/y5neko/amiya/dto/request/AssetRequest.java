package com.y5neko.amiya.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AssetRequest implements Serializable {

    @NotBlank(message = "资产名称不能为空", groups = {Create.class, Update.class})
    private String name;

    @NotBlank(message = "资产IP不能为空", groups = {Create.class, Update.class})
    private String ip;

    private String domain;

    @Min(value = 1, message = "端口号不能小于1", groups = {Create.class, Update.class})
    @Max(value = 65535, message = "端口号超出范围", groups = {Create.class, Update.class})
    private Integer port;

    @NotBlank(message = "协议不能为空", groups = {Create.class, Update.class})
    private String protocol;

    @NotEmpty(message = "标签不能为空")
    private List<@NotBlank(message = "标签项不能为空") String> tags;


    @NotNull(message = "资产所有者不能为空", groups = {Create.class, Update.class})
    private Long ownerId;

    @NotBlank(message = "资产状态不能为空", groups = {Create.class, Update.class})
    private String status;

    /** 用于区分新增/更新的校验组 */
    public interface Create {}
    public interface Update {}
}
