package com.notsay.dingtalkalarm.web.controller;

import com.notsay.dingtalkalarm.service.ShellCommandExecService;
import com.notsay.dingtalkalarm.web.param.ShellDistanceParam;
import com.notsay.dingtalkalarm.web.param.ShellLocalParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author by dsy
 * @Classname ShellController
 * @Description TODO
 * @Date 2023/2/15 14:33
 */
@RestController
@RequestMapping("/shell")
public class ShellController {

    @Autowired
    ShellCommandExecService shellCommandExecService;

    @PostMapping("/local")
    public Map<String, String> local(@RequestBody ShellLocalParam param) {
        return shellCommandExecService.runLocalShell(param.getCommandList());
    }

    @PostMapping("/distance")
    public Map<String, String> distance(@RequestBody ShellDistanceParam param) {
        Map<String, String> stringStringMap = shellCommandExecService.runDistanceShell(param.getCommandList(), param.getUser(), param.getPasswd(), param.getHost());
        String rating = shellCommandExecService.disposeMemShellResult(stringStringMap.get("free -m"));
        System.out.println("内存使用率为: " + rating);

        String diskRate = shellCommandExecService.disposeDiskShellResult(stringStringMap.get("df -hl"));
        System.out.println("磁盘使用率为：" + diskRate);
        return stringStringMap;
    }

}
