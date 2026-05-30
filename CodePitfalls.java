package com.test.pitfalls;

import java.util.*;
import java.util.concurrent.*;

/**
 * 包含各种潜在问题的测试类
 */
public class CodePitfalls {
    
    // 坑1: 使用可变对象作为HashMap的key
    private Map<User, String> userMap = new HashMap<>();
    
    // 坑2: 静态集合导致的内存泄漏
    private static List<String> staticList = new ArrayList<>();
    
    // 坑3: 未正确初始化的线程池
    private ExecutorService executorService;
    
    /**
     * 坑1: 可变对象作为HashMap key - 修改key后无法获取value
     */
    public void testMutableKey() {
        User user = new User("Alice", 25);
        userMap.put(user, "admin");
        
        // 修改了user的字段，导致hashCode变化
        user.setName("Bob");
        
        // 这里会返回null，因为hashCode已经变了
        String role = userMap.get(user);
        System.out.println("Role: " + role); // null!
    }
    
    /**
     * 坑2: 静态集合无限增长导致内存泄漏
     */
    public void addToList(String item) {
        staticList.add(item);
        // 从来没有清理机制，会导致OutOfMemoryError
    }
    
    /**
     * 坑3: 线程池未初始化就使用
     */
    public void executeTask(Runnable task) {
        // executorService从未初始化，会抛出NullPointerException
        executorService.submit(task);
    }
    
    /**
     * 坑4: 资源未关闭 - FileInputStream泄漏
     */
    public void readFile(String path) throws Exception {
        java.io.FileInputStream fis = new java.io.FileInputStream(path);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        // 忘记关闭流！如果发生异常，流也不会被关闭
        System.out.println(new String(data));
    }
    
    /**
     * 坑5: 并发修改异常
     */
    public void removeFromListWhileIterating() {
        List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c", "d"));
        
        // ConcurrentModificationException!
        for (String item : list) {
            if ("b".equals(item)) {
                list.remove(item);
            }
        }
    }
    
    /**
     * 坑6: 自动装箱的性能问题和NPE风险
     */
    public Integer calculateSum(Integer a, Integer b) {
        // 如果a或b为null，会抛出NullPointerException
        return a + b;
    }
    
    /**
     * 坑7: 字符串比较错误
     */
    public boolean checkRole(String userRole) {
        // 如果userRole为null，会抛出NPE
        // 应该用 "ADMIN".equals(userRole)
        return userRole.equals("ADMIN");
    }
    
    /**
     * 坑8: 浮点数精度问题
     */
    public boolean checkPrice(double price1, double price2) {
        // 直接用==比较double是不准确的
        return price1 == price2;
    }
    
    /**
     * 坑9: 数组越界
     */
    public String getFirstElement(String[] array) {
        // 没有检查数组是否为空或长度为0
        return array[0];
    }
    
    /**
     * 坑10: SQL注入漏洞
     */
    public String buildQuery(String username) {
        // 严重的SQL注入漏洞！
        return "SELECT * FROM users WHERE username = '" + username + "'";
    }
    
    /**
     * 坑11: 竞态条件 - 非线程安全的单例
     */
    private static Singleton instance;
    
    public static Singleton getInstance() {
        if (instance == null) {
            // 多线程环境下可能创建多个实例
            instance = new Singleton();
        }
        return instance;
    }
    
    /**
     * 坑12: 死锁风险
     */
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    public void methodA() {
        synchronized (lock1) {
            synchronized (lock2) {
                // do something
            }
        }
    }
    
    public void methodB() {
        synchronized (lock2) {
            synchronized (lock1) {
                // do something - 可能与methodA形成死锁！
            }
        }
    }
    
    /**
     * 坑13: catch块吞掉异常
     */
    public void swallowException() {
        try {
            // some code that might throw exception
            throw new RuntimeException("Error!");
        } catch (Exception e) {
            // 什么都没做！异常被 silently swallowed
        }
    }
    
    /**
     * 坑14: 在循环中创建不必要的对象
     */
    public void inefficientConcat(String[] words) {
        String result = "";
        for (String word : words) {
            // 每次循环都创建新的String对象，性能极差
            result += word;
        }
    }
    
    /**
     * 坑15: 浅拷贝问题
     */
    public List<String> getListCopy(List<String> original) {
        // 这只是浅拷贝，修改原列表会影响"副本"
        return new ArrayList<>(original);
    }
    
    // 内部类用于演示
    static class User {
        private String name;
        private int age;
        
        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof User)) return false;
            User other = (User) obj;
            return age == other.age && Objects.equals(name, other.name);
        }
    }
    
    static class Singleton {
        private Singleton() {}
    }
}
