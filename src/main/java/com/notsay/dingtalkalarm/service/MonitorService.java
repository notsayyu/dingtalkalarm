package com.notsay.dingtalkalarm.service;

/**
 * @author by dsy
 * @Classname MonitorService
 * @Description TODO
 * @Date 2022/9/20 16:44
 */
public interface MonitorService {
    /**
     * 检查服务运行状态
     */
    void monitorServerStatus();

    /**
     * 监测服务器硬件资源
     */
    void ecsMonitor();

}
