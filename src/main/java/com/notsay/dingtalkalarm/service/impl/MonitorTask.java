package com.notsay.dingtalkalarm.service.impl;

import com.notsay.dingtalkalarm.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author by dsy
 * @Classname MonitorTask
 * @Description TODO
 * @Date 2022/9/20 16:39
 */
//@Component
@Slf4j
public class MonitorTask {

    @Autowired
    private MonitorService monitorService;

    /**
     * 监测服务运行状态
     */
    @Scheduled(cron = "${server.status.monitor.cron}")
    public void serverStatusMonitor() {
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
        long startTime = System.currentTimeMillis();
        log.info("*** 监测服务器硬件资源开始，当前时间:[{}]", startTime);
        monitorService.monitorServerStatus();
        long endTime = System.currentTimeMillis();
        log.info("*** 监测服务器硬件资源结束，当前时间:[{}],执行时间为[{}]", endTime, endTime - startTime);
    }
}
