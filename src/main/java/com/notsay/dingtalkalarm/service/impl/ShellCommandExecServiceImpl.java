package com.notsay.dingtalkalarm.service.impl;

import com.jcraft.jsch.*;
import com.notsay.dingtalkalarm.service.ShellCommandExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public static final String CPU_MEM_SHELL = "top -b -n 1";
    public static final String FILES_SHELL = "df -hl";
    public static final String[] COMMANDS = {CPU_MEM_SHELL, FILES_SHELL};
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
