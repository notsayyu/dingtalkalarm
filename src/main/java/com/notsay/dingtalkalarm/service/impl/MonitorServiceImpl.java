package com.notsay.dingtalkalarm.service.impl;

import com.notsay.dingtalkalarm.common.config.CommonConfig;
import com.notsay.dingtalkalarm.common.dto.DingTalkRequestDto;
import com.notsay.dingtalkalarm.common.dto.MsgDto;
import com.notsay.dingtalkalarm.common.util.ExternalHttpClient;
import com.notsay.dingtalkalarm.common.util.JsonUtils;
import com.notsay.dingtalkalarm.service.DingTalkService;
import com.notsay.dingtalkalarm.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

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


    /**
     * 检查服务运行状态
     */
    @Override
    public void monitorServerStatus() {
        Integer httpStatus = httpClient.doGet(commonConfig.getHealthUrl(), new HashMap<>(), new HashMap<>());
        log.info("调用健康地址返回的httpStats为:{}", httpStatus);
        if(HttpStatus.SC_OK != httpStatus){
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

    }
}
