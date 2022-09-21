package com.notsay.dingtalkalarm.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by dsy
 * @Classname DingTalkRequestDto
 * @Description TODO
 * @Date 2022/9/21 13:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DingTalkRequestDto {
    /**
     * 消息类型
     */
    private String msgtype;

    /**
     * 具体消息
     */
    private MsgDto text;
}
