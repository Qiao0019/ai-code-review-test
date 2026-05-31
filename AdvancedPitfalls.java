package com.test.pitfalls;

import java.util.*;

/**
 * 包含更多高级陷阱的测试类
 */
public class AdvancedPitfalls {
    
    /**
     * 坑1: 反射破坏封装性
     */
    public void breakEncapsulation() throws Exception {
        String secret = "password123";
        
        // 通过反射访问私有字段
        java.lang.reflect.Field field = String.class.getDeclaredField("value");
        field.setAccessible(true);
        char[] chars = (char[]) field.get(secret);
        
        // 修改不可变的String！
        Arrays.fill(chars, 'x');
        System.out.println(secret); // xxxxxxxxxx
    }
    
    /**
     * 坑2: 枚举的反序列化问题
     */
    enum SingletonEnum {
        INSTANCE;
        
        private String data;
        
        public String getData() {
            return data;
        }
        
        public void setData(String data) {
            this.data = data;
        }
    }
    
    /**
     * 坑3: Cloneable接口的陷阱
     */
    static class BadClone implements Cloneable {
        private List<String> list = new ArrayList<>();
        
        @Override
        protected Object clone() throws CloneNotSupportedException {
            // 浅拷贝！list引用被共享
            return super.clone();
        }
    }
    
    /**
     * 坑4: Comparable违反约定
     */
    static class BadComparable implements Comparable<BadComparable> {
        private int value;
        
        @Override
        public int compareTo(BadComparable other) {
            // 违反自反性：x.compareTo(x)应该返回0
            if (this.value == other.value) {
                return 1; // 错误！
            }
            return Integer.compare(this.value, other.value);
        }
    }
    
    /**
     * 坑5: 泛型擦除导致的方法签名冲突
     */
    static class GenericConflict {
        public void process(List<String> list) {
            System.out.println("String list");
        }
        
        // 编译错误：由于类型擦除，这两个方法签名相同
        // public void process(List<Integer> list) {
        //     System.out.println("Integer list");
        // }
    }
    
    /**
     * 坑6: Annotation的默认值陷阱
     */
    @interface MyAnnotation {
        String value() default "";
        int count() default 0;
    }
    
    /**
     * 坑7: 内部类持有外部类引用导致内存泄漏
     */
    class InnerClass {
        // 隐式持有OuterClass.this引用
        // 如果InnerClass生命周期长于OuterClass，会导致内存泄漏
        private Runnable longLivedTask = () -> {
            // 这里可以访问外部类的所有成员
            doSomething();
        };
    }
    
    private void doSomething() {
        // outer class method
    }
    
    /**
     * 坑8: switch语句缺少break
     */
    public String getDayType(int day) {
        String type;
        switch (day) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                type = "weekday";
                break;
            case 6:
            case 7:
                type = "weekend";
                // 忘记break！会fall through到default
            default:
                type = "invalid";
        }
        return type;
    }
    
    /**
     * 坑9: 浮点数循环条件
     */
    public void floatLoop() {
        // 由于精度问题，可能永远不会等于1.0
        for (float f = 0.0f; f != 1.0f; f += 0.1f) {
            System.out.println(f);
            // 可能无限循环！
        }
    }
    
    /**
     * 坑10: 字符串驻留导致的意外相等
     */
    public void stringInterning() {
        String s1 = "hello";
        String s2 = "hel" + "lo"; // 编译期优化，指向同一对象
        
        System.out.println(s1 == s2); // true! 可能出乎意料
    }
    
    /**
     * 坑11: 构造函数中调用可覆盖方法
     */
    static class Parent {
        public Parent() {
            initialize(); // 危险！子类可能还未初始化完成
        }
        
        protected void initialize() {
            // base initialization
        }
    }
    
    static class Child extends Parent {
        private List<String> items;
        
        @Override
        protected void initialize() {
            // items可能还是null！
            items = new ArrayList<>();
            items.add("item");
        }
    }
    
    /**
     * 坑12: 静态初始化顺序问题
     */
    static class InitializationOrder {
        static int a = b + 1;
        static int b = 10;
        
        // a的值是1而不是11，因为b在a之后初始化
    }
    
    /**
     * 坑13: 数组协变导致的运行时错误
     */
    public void arrayCovariance() {
        String[] strings = new String[10];
        Object[] objects = strings; // 合法，因为数组是协变的
        
        // 编译通过，但运行时会抛出ArrayStoreException
        objects[0] = 123;
    }
    
    /**
     * 坑14: 位运算优先级问题
     */
    public boolean checkBit(int value, int bit) {
        // &的优先级低于==，需要加括号
        return (value & (1 << bit)) != 0;
        
        // 错误写法：value & (1 << bit) == 0
        // 会被解析为：value & ((1 << bit) == 0)
    }
    
    /**
     * 坑15: 三元运算符的类型提升
     */
    public Number ternaryType(boolean condition) {
        Integer i = 1;
        Double d = 2.0;
        
        // 三元运算符会进行类型提升，结果是Double
        return condition ? i : d;
    }
    
    /**
     * 坑16: instanceof和null
     */
    public void checkInstance(Object obj) {
        // instanceof对null总是返回false
        if (obj instanceof String) {
            // 这里的代码不会执行，如果obj是null
            System.out.println(((String) obj).length());
        }
    }
    
    /**
     * 坑17: 方法重载的选择困惑
     */
    public void overloadedMethod(Object obj) {
        System.out.println("Object version");
    }
    
    public void overloadedMethod(String str) {
        System.out.println("String version");
    }
    
    public void testOverload() {
        overloadedMethod(null); // 调用String版本（更具体）
        overloadedMethod((Object) null); // 强制调用Object版本
    }
    
    /**
     * 坑18: 递归没有终止条件
     */
    public int infiniteRecursion(int n) {
        // 缺少基准情况，会导致StackOverflowError
        return infiniteRecursion(n - 1);
    }
    
    /**
     * 坑19: 集合的subList视图问题
     */
    public void subListTrap() {
        List<Integer> original = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        List<Integer> sub = original.subList(1, 4);
        
        // 修改sub会影响original
        sub.set(0, 99);
        System.out.println(original); // [1, 99, 3, 4, 5]
        
        // 修改original的结构会导致sub失效
        original.add(6);
        // sub.get(0); // ConcurrentModificationException!
    }
    
    /**
     * 坑20: System.identityHashCode的误用
     */
    public void identityHashCodeTrap() {
        String s1 = new String("hello");
        String s2 = new String("hello");
        
        // identityHashCode相同不代表是同一个对象（哈希碰撞）
        // 不同对象的identityHashCode一定不同？错！也可能碰撞
        System.out.println(System.identityHashCode(s1));
        System.out.println(System.identityHashCode(s2));
    }
}
