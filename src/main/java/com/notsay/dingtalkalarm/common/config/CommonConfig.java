package com.notsay.dingtalkalarm.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author by dsy
 * @Classname CommonConfig
 * @Description TODO
 * @Date 2022/9/20 16:34
 */
@Configuration
@Data
public class CommonConfig {
    /**
     * webhook地址
     */
    @Value("${dingtalk.webhook}")
    private String webhook;

    /**
     * 健康地址
     */
    @Value("${server.health.url}")
    private String healthUrl;


    /**
     * 应用名称
     */
    @Value("${app.name}")
    private String appName;

    /**
     * 服务器内存使用率阈值
     */
    @Value("${ecs.mem.used.threshold}")
    private String memUsedThreshold;

    /**
     * 服务器磁盘使用率阈值
     */
    @Value("${ecs.disk.used.threshold}")
    private String diskUsedThreshold;

    /**
     * 所监控的服务器ip
     */
    @Value("${ecs.used.monitor.ip}")
    private String ecsIp;
}
