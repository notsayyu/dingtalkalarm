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
     * 检查内存使用的命令
     */
    @Value("${memory.used.command}")
    private String memoryUsedCommand;

    /**
     * 检查磁盘使用的命令
     */
    @Value("${disk.used.command}")
    private String diskUsedCommand;
}
