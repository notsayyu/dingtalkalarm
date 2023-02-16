package com.notsay.dingtalkalarm.service.impl;

import com.notsay.dingtalkalarm.common.config.CommonConfig;
import com.notsay.dingtalkalarm.common.constant.CommonConstants;
import com.notsay.dingtalkalarm.common.util.ExternalHttpClient;
import com.notsay.dingtalkalarm.service.DingTalkService;
import com.notsay.dingtalkalarm.service.MonitorService;
import com.notsay.dingtalkalarm.service.ShellCommandExecService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author by dsy
 * @Classname MonitorServiceImpl
 * @Description TODO
 * @Date 2022/9/20 16:45
 */
@Service
@Slf4j
public class MonitorServiceImpl implements MonitorService {
    @Autowired
    private ExternalHttpClient httpClient;
    @Autowired
    private CommonConfig commonConfig;

    @Autowired
    private DingTalkService dingTalkService;
    @Autowired
    private ShellCommandExecService shellCommandExecService;


    /**
     * 检查服务运行状态
     */
    @Override
    public void monitorServerStatus() {
        Integer httpStatus = httpClient.doGet(commonConfig.getHealthUrl(), new HashMap<>(), new HashMap<>());
        log.info("调用健康地址返回的httpStats为:{}", httpStatus);
        if (HttpStatus.SC_OK != httpStatus) {
            //进行钉钉消息处理
            String text = commonConfig.getAppName() + " 服务异常，请检查！ @所有人";
            dingTalkService.sendTextWarn(text);
        }

    }

    /**
     * 监测服务器硬件资源
     */
    @Override
    public void ecsMonitor() {
        //1、选择要监控的资源
        List<String> commandList = new ArrayList<>();
        commandList.add(CommonConstants.MEM_SHELL);
        commandList.add(CommonConstants.DISK_SHELL);

        //2、获取返回的信息
        Map<String, String> commandResultMap = shellCommandExecService.runLocalShell(commandList);
        
        Set<String> commandSet = commandResultMap.keySet();

        //3、根据具体的命令进行处理
        for (String command : commandSet) {

            if (CommonConstants.MEM_SHELL.equals(command)) {
                //如果是内存占用命令
                dealMem(commandResultMap.get(command));
            }
            if (CommonConstants.DISK_SHELL.equals(command)) {
                //如果是（系统）磁盘占用命令
                dealDisk(commandResultMap.get(command));
            }

        }
    }

    private void dealMem(String commandResult) {
        //解析并获得内存占用
        String rating = shellCommandExecService.disposeMemShellResult(commandResult);
        if (!StringUtils.hasText(rating)) {
            log.error("获取内存占用为空");
            return;
        }
        //判断是否达到阈值
        BigDecimal rate = new BigDecimal(rating);
        BigDecimal threshold = new BigDecimal(commonConfig.getMemUsedThreshold());
        if (rate.compareTo(threshold) > 0) {
            //占用率超过阈值，发送钉钉消息
            //进行钉钉消息处理
            String text = "服务器[" + commonConfig.getEcsIp() + "]内存使用率[" + rating + "]过阈值[" + commonConfig.getMemUsedThreshold() + "]，请检查！ @所有人";
            dingTalkService.sendTextWarn(text);
        }

    }

    private void dealDisk(String commandResult) {
        //解析并获得磁盘占用
        String rating = shellCommandExecService.disposeDiskShellResult(commandResult);
        if (!StringUtils.hasText(rating)) {
            log.error("获取磁盘占用为空");
            return;
        }
        //判断是否达到阈值
        BigDecimal rate = new BigDecimal(rating);
        BigDecimal threshold = new BigDecimal(commonConfig.getDiskUsedThreshold());
        if (rate.compareTo(threshold) > 0) {
            //占用率超过阈值，发送钉钉消息
            //进行钉钉消息处理
            String text = "服务器[" + commonConfig.getEcsIp() + "]磁盘使用率[" + rating + "]过阈值[" + commonConfig.getDiskUsedThreshold() + "]，请检查！ @所有人";
            dingTalkService.sendTextWarn(text);
        }
    }
}
