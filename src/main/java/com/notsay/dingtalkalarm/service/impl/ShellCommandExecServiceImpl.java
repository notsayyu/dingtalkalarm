package com.notsay.dingtalkalarm.service.impl;

import com.jcraft.jsch.*;
import com.notsay.dingtalkalarm.service.ShellCommandExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author by dsy
 * @Classname ShellCommandExecServiceImpl
 * @Description TODO
 * https://blog.csdn.net/jack_bob/article/details/124170689
 * @Date 2023/2/15 10:22
 */
@Service
@Slf4j
public class ShellCommandExecServiceImpl implements ShellCommandExecService {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static Session session;

    /**
     * 远程连接Linux 服务器 执行相关的命令
     *
     * @param commands 执行的脚本
     * @param user     远程连接的用户名
     * @param passwd   远程连接的密码
     * @param host     远程连接的主机IP
     * @return 最终命令返回信息
     */
    @Override
    public Map<String, String> runDistanceShell(List<String> commands, String user, String passwd, String host) {
        if (!connect(user, passwd, host, 22)) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        StringBuilder stringBuffer;

        BufferedReader reader = null;
        Channel channel = null;
        InputStream in = null;
        try {
            for (String command : commands) {
                stringBuffer = new StringBuilder();
                channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(command);

                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);

                channel.connect();

                in = channel.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String buf;
                while ((buf = reader.readLine()) != null) {

                    //舍弃PID 进程信息
                    if (buf.contains("PID")) {
                        break;
                    }
                    stringBuffer.append(buf.trim()).append(LINE_SEPARATOR);
                }
                //每个命令存储自己返回数据-用于后续对返回数据进行处理
                map.put(command, stringBuffer.toString());
            }
        } catch (IOException | JSchException e) {
            log.error("执行服务器命令失败", e);
        } finally {
            try {
                if (Objects.nonNull(reader)) {
                    reader.close();
                }
                if (Objects.nonNull(in)) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("执行服务器命令发生IO异常", e);
            }
            if (Objects.nonNull(channel)) {
                channel.disconnect();
            }
            session.disconnect();
        }
        return map;
    }

    /**
     * 直接在本地执行 shell
     *
     * @param commands 执行的脚本
     * @return 执行结果信息
     */
    @Override
    public Map<String, String> runLocalShell(List<String> commands) {
        Runtime runtime = Runtime.getRuntime();

        Map<String, String> map = new HashMap<>();
        StringBuilder stringBuffer;

        BufferedReader reader = null;
        Process process = null;
        InputStream inputStream = null;
        for (String command : commands) {
            stringBuffer = new StringBuilder();
            try {
                process = runtime.exec(command);
                inputStream = process.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String buf;
                while ((buf = reader.readLine()) != null) {
                    //舍弃PID 进程信息
                    if (buf.contains("PID")) {
                        break;
                    }
                    stringBuffer.append(buf.trim()).append(LINE_SEPARATOR);
                }

            } catch (IOException e) {
                log.error("执行服务器命令失败", e);
            } finally {
                try {
                    if (Objects.nonNull(reader)) {
                        reader.close();
                    }
                    if (Objects.nonNull(inputStream)) {
                        inputStream.close();
                    }
                    if (Objects.nonNull(process)) {
                        process.destroy();
                    }
                } catch (IOException e) {
                    log.error("执行服务器命令发生IO异常", e);
                }
            }
            //每个命令存储自己返回数据-用于后续对返回数据进行处理
            map.put(command, stringBuffer.toString());
        }
        return map;
    }

    /**
     * 处理top命令 top -b -n 1
     * //TODO 待完善和检查测试
     *
     * @param commandResult
     */
    @Override
    public String disposeCpuMemShellResult(String commandResult) {
        StringBuilder buffer = new StringBuilder();
        String[] strings = commandResult.split(LINE_SEPARATOR);
        //将返回结果按换行符分割
        for (String line : strings) {
            //转大写处理
            line = line.toUpperCase();

            //处理CPU Cpu(s): 10.8%us,  0.9%sy,  0.0%ni, 87.6%id,  0.7%wa,  0.0%hi,  0.0%si,  0.0%st
            if (line.startsWith("CPU(S):")) {
                String cpuStr = "CPU 用户使用占有率:";
                try {
                    cpuStr += line.split(":")[1].split(",")[0].replace("US", "");
                } catch (Exception e) {
                    log.error("计算cpu占用出错", e);
                    cpuStr += "计算过程出错";
                }
                buffer.append(cpuStr).append(LINE_SEPARATOR);

                //处理内存 Mem:  66100704k total, 65323404k used,   777300k free,    89940k buffers
            } else if (line.startsWith("MEM")) {
                String memStr = "内存使用情况:";
                try {
                    memStr += line.split(":")[1]
                            .replace("TOTAL", "总计")
                            .replace("USED", "已使用")
                            .replace("FREE", "空闲")
                            .replace("BUFFERS", "缓存");

                } catch (Exception e) {
                    e.printStackTrace();
                    memStr += "计算过程出错";
                    buffer.append(memStr).append(LINE_SEPARATOR);
                    continue;
                }
                buffer.append(memStr).append(LINE_SEPARATOR);

            }
        }
        return buffer.toString();
    }

    /**
     * 处理内存统计命令 free -m
     * <p>
     * total        used        free      shared  buff/cache   available
     * Mem:           1838         260         192          19        1385        1367
     * Swap:             0           0           0
     *
     * @param commandResult
     */
    @Override
    public String disposeMemShellResult(String commandResult) {
        if (!StringUtils.hasText(commandResult)) {
            return null;
        }
        String[] strings = commandResult.split(LINE_SEPARATOR);
        try {
            for (String result : strings) {
                if (result.startsWith("Mem")) {
                    String numLine = result.split(":")[1];
                    String[] nums = numLine.trim().split("\\s+");
                    BigDecimal total = new BigDecimal(nums[0]);
                    BigDecimal available = new BigDecimal(nums[5]);
                    BigDecimal vacancyRate = available.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

                    BigDecimal rating = new BigDecimal("100").subtract(vacancyRate);
                    log.info("内存使用率为:{}", rating);
                    return rating.toString();
                }
            }
        } catch (Exception e) {
            log.error("计算内存使用率出错", e);
        }
        return null;
    }

    /**
     * 处理内存统计命令 df -hl
     * Filesystem      Size  Used Avail Use% Mounted on
     * /dev/vda1        40G   20G   18G  53% /
     * devtmpfs        909M     0  909M   0% /dev
     * tmpfs           920M   19M  901M   3% /dev/shm
     * tmpfs           920M  616K  919M   1% /run
     * tmpfs           920M     0  920M   0% /sys/fs/cgroup
     * tmpfs           184M     0  184M   0% /run/user/0
     * overlay          40G   20G   18G  53% /var/lib/docker/overlay2/2b4b7ac9502adee20a8aceade720af387a9148113f1487d9b31664ad481f702f/merged
     *
     * @param commandResult
     */
    @Override
    public String disposeDiskShellResult(String commandResult) {
        if (!StringUtils.hasText(commandResult)) {
            return null;
        }
        String[] strings = commandResult.split(LINE_SEPARATOR);
        try {
            for (String result : strings) {
                if (result.endsWith("/")) {
                    String[] nums = result.trim().split("\\s+");
                    String ratingString = nums[4];

                    log.info("系统磁盘使用率为:{}", ratingString);
                    return ratingString.substring(0, ratingString.length() - 1);
                }
            }
        } catch (Exception e) {
            log.error("计算系统磁盘使用率出错", e);
        }
        return null;
    }

    public static void main(String[] args) {
        String s = "Mem:           1838         262         197          19        1378        1365";
        String num = s.split(":")[1];
        String[] nums = num.trim().split("\\s+");
        System.out.println(num);
    }

    /**
     * 连接到指定的HOST
     *
     * @return isConnect
     * @throws JSchException JSchException
     */
    private static boolean connect(String user, String passwd, String host, int port) {
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(passwd);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
        } catch (JSchException e) {
            log.error("connect {} error !", host, e);
            return false;
        }
        return true;
    }

}
