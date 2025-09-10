

package org.example.jvmchaos.controller;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ChaosController {
    private static final List<byte[]> memoryLeakList = new ArrayList<>();

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // 模拟堆内存溢出
    @GetMapping("/oom")
    public String oom() {
        List<byte[]> list = new ArrayList<>();
        while (true) {
            list.add(new byte[10 * 1024 * 1024]); // 每次分配 10MB
        }
    }

    // 模拟栈溢出
    @GetMapping("/so")
    public String so() {
        return recurse(0);
    }

    private String recurse(int depth) {
        return recurse(depth + 1); // 无限递归
    }

    // 模拟线程泄漏
    @GetMapping("/thread-leak")
    public String threadLeak() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }).start();
        return "thread started, current thread may leak";
    }

    // 模拟内存泄漏（静态集合持有对象）
    @GetMapping("/mem-leak")
    public String memLeak() {
        memoryLeakList.add(new byte[5 * 1024 * 1024]); // 每次 5MB
        return "allocated 5MB, total: " + memoryLeakList.size() + " objects";
    }

    // 模拟 CPU 100% 占用
    @GetMapping("/cpu-spike")
    public String cpuSpike() {
        new Thread(() -> {
            while (true) {
                double x = Math.random() * Math.random(); // 空转
            }
        }).start();
        return "CPU spike thread started";
    }

    // 模拟阻塞（卡住请求线程）
    @GetMapping("/block")
    public String block() throws InterruptedException {
        Thread.sleep(Long.MAX_VALUE);
        return "done";
    }
}
