package com.notsay.dingtalkalarm;

import com.notsay.dingtalkalarm.service.ShellCommandExecService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
class DingtalkalarmApplicationTests {

    @Autowired
    ShellCommandExecService service;

    @Test
    void contextLoads() {
    }


    @Test
    public void shellTest() {
        List<String> commands = new ArrayList<>();
        commands.add("free -m");
        commands.add("df -h");
        commands.add("top -b -n 1");

        Map<String, String> root = service.runDistanceShell(commands, "root", "hyperchain@dsy1", "118.31.59.77");
        System.out.println(root);
    }
}
