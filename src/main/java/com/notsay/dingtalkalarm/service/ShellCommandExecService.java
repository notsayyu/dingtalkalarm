package com.notsay.dingtalkalarm.service;

import java.util.List;
import java.util.Map;

/**
 * @author by dsy
 * @Classname ShellCommandExecService
 * @Description TODO
 * @Date 2023/2/15 10:19
 */
public interface ShellCommandExecService {

    /**
     * 远程连接Linux 服务器 执行相关的命令
     *
     * @param commands 执行的脚本
     * @param user     远程连接的用户名
     * @param passwd   远程连接的密码
     * @param host     远程连接的主机IP
     * @return 最终命令返回信息
     */
    Map<String, String> runDistanceShell(List<String> commands, String user, String passwd, String host);

    /**
     * 直接在本地执行 shell
     *
     * @param commands 执行的脚本
     * @return 执行结果信息
     */
    Map<String, String> runLocalShell(List<String> commands);

    /**
     * 处理top命令 top -b -n 1
     */
    String disposeCpuMemShellResult(String commandResult);

    /**
     * 处理内存统计命令 free -m
     */
    String disposeMemShellResult(String commandResult);

    /**
     * 处理内存统计命令 free -m
     */
    String disposeDiskShellResult(String commandResult);
}
