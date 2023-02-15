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
public class ShellDistanceParam {
    /**
     * 命令集合
     */
    List<String> commandList;

    /**
     * 用户名
     */
    String user;

    /**
     * 密码
     */
    String passwd;

    /**
     * 地址
     */
    String host;
}
