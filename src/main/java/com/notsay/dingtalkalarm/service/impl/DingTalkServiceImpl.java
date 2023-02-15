package com.notsay.dingtalkalarm.service.impl;

import com.notsay.dingtalkalarm.common.config.CommonConfig;
import com.notsay.dingtalkalarm.common.dto.DingTalkRequestDto;
import com.notsay.dingtalkalarm.common.dto.MsgDto;
import com.notsay.dingtalkalarm.common.util.ExternalHttpClient;
import com.notsay.dingtalkalarm.common.util.JsonUtils;
import com.notsay.dingtalkalarm.service.DingTalkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author by dsy
 * @Classname DingTalkServiceImpl
 * @Description TODO
 * @Date 2023/2/15 10:09
 */
@Service
@Slf4j
public class DingTalkServiceImpl implements DingTalkService {
    @Autowired
    private ExternalHttpClient httpClient;

    @Autowired
    private CommonConfig commonConfig;

    /**
     * 发送文本警告消息 注意消息匹配规则，如关键字
     *
     * @param text
     */
    @Override
    public String sendTextWarn(String text) {
        //进行钉钉消息处理
        //构建请求提dto
        DingTalkRequestDto dingTalkRequestDto = new DingTalkRequestDto();
        dingTalkRequestDto.setMsgtype("text");

        MsgDto msgDto = new MsgDto();
        msgDto.setContent(text);
        dingTalkRequestDto.setText(msgDto);

        String responseBody = httpClient.doPost(commonConfig.getWebhook(), JsonUtils.toJSONString(dingTalkRequestDto), new HashMap<>());
        log.info("请求钉钉返回结果为:{}", responseBody);
        return responseBody;
    }
}
