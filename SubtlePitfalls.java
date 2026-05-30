package com.test.pitfalls;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 包含更多隐蔽陷阱的测试类
 */
public class SubtlePitfalls {
    
    /**
     * 坑1: String.intern()的误用
     */
    public void testStringIntern() {
        String s1 = new String("hello") + new String(" world");
        String s2 = "hello world";
        
        // s1和s2是不同的对象引用
        System.out.println(s1 == s2); // false
        
        // 使用intern后可能相等，但行为依赖JVM实现
        System.out.println(s1.intern() == s2); // 可能是true或false
    }
    
    /**
     * 坑2: BigDecimal构造函数的精度问题
     */
    public void testBigDecimal() {
        // 使用double构造函数会有精度问题
        java.math.BigDecimal bd1 = new java.math.BigDecimal(0.1);
        // 应该使用String构造函数
        java.math.BigDecimal bd2 = new java.math.BigDecimal("0.1");
        
        System.out.println(bd1); // 0.1000000000000000055511151231257827021181583404541015625
        System.out.println(bd2); // 0.1
    }
    
    /**
     * 坑3: Arrays.asList返回的列表不可修改
     */
    public void testImmutableList() {
        List<String> list = Arrays.asList("a", "b", "c");
        
        // UnsupportedOperationException!
        list.add("d");
    }
    
    /**
     * 坑4: Stream只能消费一次
     */
    public void testStreamReuse() {
        List<String> list = Arrays.asList("a", "b", "c");
        java.util.stream.Stream<String> stream = list.stream();
        
        // 第一次使用正常
        long count1 = stream.count();
        
        // IllegalStateException! Stream已经被消费过了
        long count2 = stream.count();
    }
    
    /**
     * 坑5: Optional.get()之前不检查
     */
    public String getUserName(Optional<String> name) {
        // 如果Optional为空，会抛出NoSuchElementException
        return name.get(); // 应该先用isPresent()检查或用orElse()
    }
    
    /**
     * 坑6: HashMap初始容量设置不当
     */
    public void testHashMapCapacity() {
        // 如果需要存储100个元素，应该设置合适的初始容量和负载因子
        Map<String, String> map = new HashMap<>(); // 默认容量16，会多次rehash
        
        // 更好的做法：new HashMap<>(128, 0.75f)
        for (int i = 0; i < 100; i++) {
            map.put("key" + i, "value" + i);
        }
    }
    
    /**
     * 坑7: 比较器违反约定
     */
    public void testComparator() {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9);
        
        // 这个比较器违反了传递性，可能导致排序异常
        Comparator<Integer> badComparator = (a, b) -> {
            if (a % 2 == 0) return -1;
            if (b % 2 == 0) return 1;
            return 0; // 奇数之间都认为相等！
        };
        
        Collections.sort(numbers, badComparator);
    }
    
    /**
     * 坑8: 在增强for循环中修改集合结构
     */
    public void testConcurrentModification() {
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        
        // ConcurrentModificationException!
        for (String key : map.keySet()) {
            if ("b".equals(key)) {
                map.remove(key);
            }
        }
    }
    
    /**
     * 坑9: ThreadLocal内存泄漏
     */
    private static ThreadLocal<List<String>> threadLocal = new ThreadLocal<>();
    
    public void useThreadLocal() {
        // 设置了值但从未清理
        threadLocal.set(new ArrayList<>());
        
        // 在线程池环境中，线程会被复用，导致内存泄漏
        // 应该在finally块中调用threadLocal.remove()
    }
    
    /**
     * 坑10: 自动装箱缓存陷阱
     */
    public void testIntegerCache() {
        Integer a = 127;
        Integer b = 127;
        System.out.println(a == b); // true (缓存范围内)
        
        Integer c = 128;
        Integer d = 128;
        System.out.println(c == d); // false (超出缓存范围)
        
        // 应该用equals()比较
    }
    
    /**
     * 坑11: Date的可变性
     */
    public void testDateMutability() {
        Date originalDate = new Date();
        Date copiedDate = originalDate; // 只是引用拷贝
        
        // 修改copiedDate会影响originalDate！
        copiedDate.setTime(0);
        
        System.out.println(originalDate); // Thu Jan 01 08:00:00 CST 1970
    }
    
    /**
     * 坑12: 正则表达式性能问题（灾难性回溯）
     */
    public boolean testRegex(String input) {
        // 这个正则表达式在某些输入下会导致指数级时间复杂度
        String regex = "(a+)+b";
        return input.matches(regex);
    }
    
    /**
     * 坑13: 序列化版本UID缺失
     */
    static class SerializableClass implements java.io.Serializable {
        // 缺少private static final long serialVersionUID
        // 当类结构改变时，反序列化会失败
        private String data;
    }
    
    /**
     * 坑14: 泛型类型擦除导致的运行时错误
     */
    public void testTypeErasure() {
        List<String> stringList = new ArrayList<>();
        List<Integer> intList = new ArrayList<>();
        
        // 编译后两者的类型都是List，无法区分
        System.out.println(stringList.getClass() == intList.getClass()); // true
    }
    
    /**
     * 坑15: Lambda表达式中的变量捕获
     */
    public void testLambdaCapture() {
        List<Runnable> runnables = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            // lambda捕获的是变量i的引用，不是值
            runnables.add(() -> System.out.println(i));
        }
        
        // 所有runnable都会打印相同的值（最后一个i的值）
        runnables.forEach(Runnable::run);
    }
    
    /**
     * 坑16: equals和hashCode不一致
     */
    static class InconsistentClass {
        private String value;
        
        public InconsistentClass(String value) {
            this.value = value;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof InconsistentClass)) return false;
            return Objects.equals(value, ((InconsistentClass) obj).value);
        }
        
        // hashCode实现错误：总是返回相同值
        @Override
        public int hashCode() {
            return 42; // 虽然合法，但会导致HashMap性能退化为O(n)
        }
    }
    
    /**
     * 坑17: 子类覆盖方法时的协变返回类型陷阱
     */
    static class Parent {
        public Number getValue() {
            return 42;
        }
    }
    
    static class Child extends Parent {
        @Override
        public Integer getValue() { // 协变返回类型
            return 42;
        }
    }
    
    /**
     * 坑18: try-with-resources的错误使用
     */
    public void testTryWithResources() {
        // 资源在try语句中创建，但不在try-with-resources中管理
        java.io.BufferedReader reader = null;
        try {
            reader = new java.io.BufferedReader(new java.io.FileReader("test.txt"));
            reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 忘记关闭reader
    }
    
    /**
     * 坑19: 数组和泛型的不兼容
     */
    public void testArrayAndGenerics() {
        // 编译错误：不能创建泛型数组
        // List<String>[] array = new List<String>[10];
        
        // 只能这样，但不安全
        List<String>[] unsafeArray = (List<String>[]) new List[10];
    }
    
    /**
     * 坑20: finalize方法的误用
     */
    static class FinalizeAbuse {
        @Override
        protected void finalize() throws Throwable {
            // finalize的执行时机不确定，不应该用于资源清理
            System.out.println("Cleaning up...");
            super.finalize();
        }
    }
}
