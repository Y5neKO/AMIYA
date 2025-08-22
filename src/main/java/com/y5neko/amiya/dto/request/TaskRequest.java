package com.y5neko.amiya.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class TaskRequest implements Serializable {

    @NotBlank(message = "任务名称不能为空", groups = {Create.class, Update.class})
    @Size(max = 128, message = "任务名称不能超过128个字符", groups = {Create.class, Update.class})
    private String name;

    @NotNull(message = "资产ID不能为空", groups = {Create.class, Update.class})
    private Long assetId;

    @NotBlank(message = "扫描类型不能为空", groups = {Create.class, Update.class})
    @Size(max = 64, message = "扫描类型不能超过64个字符", groups = {Create.class, Update.class})
    @Pattern(regexp = "full_scan|port_scan|vuln_scan|custom",
            message = "扫描类型只能是 full_scan / port_scan / vuln_scan / custom",
            groups = {Create.class, Update.class})
    private String scanType;

    @NotBlank(message = "调度类型不能为空", groups = {Create.class, Update.class})
    @Size(max = 32, message = "调度类型不能超过32个字符", groups = {Create.class, Update.class})
    @Pattern(regexp = "once|cron",
            message = "调度类型只能是 once / cron",
            groups = {Create.class, Update.class})
    private String scheduleType;

    /** 仅在 scheduleType=cron 时必填并校验格式 */
    @Size(max = 128, message = "Cron 表达式过长", groups = {Create.class, Update.class})
    private String cronExpr;

    /** 任务创建者ID，由系统内部设置 */
    private Long createdBy;

    /** 用于区分新增/更新的校验组 */
    public interface Create {}
    public interface Update {}
}
