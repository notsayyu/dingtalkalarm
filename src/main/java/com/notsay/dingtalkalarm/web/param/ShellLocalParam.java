package com.notsay.dingtalkalarm.web.param;

import lombok.Data;

import java.util.List;

/**
 * @author by dsy
 * @Classname ShellLocalParam
 * @Description TODO
 * @Date 2023/2/15 14:37
 */
@Data
public class ShellLocalParam {
    /**
     * 命令集合
     */
    List<String> commandList;
}
