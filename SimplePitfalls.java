package com.test.pitfalls;

import java.util.*;

/**
 * 简洁的陷阱测试类 - 约30行
 */
public class SimplePitfalls {
    
    // 坑1: 静态集合内存泄漏
    private static List<Object> cache = new ArrayList<>();
    
    public void addToCache(Object obj) {
        cache.add(obj); // 永不清理，导致OOM
    }
    
    // 坑2: NPE风险 - 自动装箱
    public int sum(Integer a, Integer b) {
        return a + b; // 如果a或b为null，抛出NPE
    }
    
    // 坑3: 资源泄漏
    public void readFile(String path) throws Exception {
        java.io.FileInputStream fis = new java.io.FileInputStream(path);
        fis.read();
        // 忘记关闭流！
    }
    
    // 坑4: 并发修改异常
    public void removeWhileIterate() {
        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
        for (String item : list) {
            if ("b".equals(item)) {
                list.remove(item); // ConcurrentModificationException!
            }
        }
    }
    
    // 坑5: SQL注入
    public String query(String username) {
        return "SELECT * FROM users WHERE name = '" + username + "'";
    }
}
