package com.notsay.dingtalkalarm.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author by dsy
 * @Classname MsgDto
 * @Description TODO
 * @Date 2022/9/21 13:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgDto {
    /**
     * 消息内容
     */
    private String content;
}
