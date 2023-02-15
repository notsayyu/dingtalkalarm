package com.notsay.dingtalkalarm.service.impl;

import com.notsay.dingtalkalarm.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author by dsy
 * @Classname MonitorTask
 * @Description TODO
 * @Date 2022/9/20 16:39
 */
@Component
@Slf4j
public class MonitorTask {

    @Autowired
    private MonitorService monitorService;

    /**
     * 服务健康检查任务是否开启
     */
    @Value("${server.status.monitor.enable:false}")
    private boolean serverStatusMonitorEnable;

    /**
     * 服务器资源检查任务是否开启
     */
    @Value("${ecs.used.monitor.enable:false}")
    private boolean ecsMonitorEnable;

    /**
     * 监测服务运行状态
     */
    @Scheduled(cron = "${server.status.monitor.cron}")
    public void serverStatusMonitor() {
        if (!serverStatusMonitorEnable) {
            return;
        }
        long startTime = System.currentTimeMillis();
        log.info("*** 监测服务运行状态开始，当前时间:[{}]", startTime);
        monitorService.monitorServerStatus();
        long endTime = System.currentTimeMillis();
        log.info("*** 监测服务运行状态结束，当前时间:[{}],执行时间为[{}]", endTime, endTime - startTime);
    }

    /**
     * 监测服务器硬件资源
     */
    @Scheduled(cron = "${ecs.used.monitor.cron}")
    public void ecsMonitor() {
        if (!ecsMonitorEnable) {
            return;
        }
        long startTime = System.currentTimeMillis();
        log.info("*** 监测服务器硬件资源开始，当前时间:[{}]", startTime);
        monitorService.ecsMonitor();
        long endTime = System.currentTimeMillis();
        log.info("*** 监测服务器硬件资源结束，当前时间:[{}],执行时间为[{}]", endTime, endTime - startTime);
    }
}
