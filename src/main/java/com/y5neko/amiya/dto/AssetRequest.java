package com.y5neko.amiya.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AssetRequest implements Serializable {

    @NotBlank(message = "资产名称不能为空", groups = Create.class)
    private String name;

    private String ip;

    private String domain;

    @Min(value = 0, message = "端口号不能为负数")
    private Integer port;

    private String protocol;

    private List<String> tags;

    private Long ownerId;

    private String status;

    /** 用于区分新增/更新的校验组 */
    public interface Create {}
    public interface Update {}
}
