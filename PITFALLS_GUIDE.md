# 代码陷阱测试集

这个项目包含各种故意编写的有问题的代码，用于测试PR工具是否能够检测和识别这些常见的编程陷阱。

## 文件说明

### 1. CodePitfalls.java
包含15个常见的基础陷阱：
- 可变对象作为HashMap key
- 静态集合内存泄漏
- 未初始化的线程池
- 资源未关闭
- 并发修改异常
- 自动装箱NPE
- 字符串比较错误
- 浮点数精度问题
- 数组越界
- SQL注入漏洞
- 非线程安全单例
- 死锁风险
- 异常被吞掉
- 循环中低效字符串拼接
- 浅拷贝问题

### 2. SubtlePitfalls.java
包含20个更隐蔽的陷阱：
- String.intern()误用
- BigDecimal精度问题
- Arrays.asList不可修改
- Stream重复使用
- Optional.get()不检查
- HashMap容量设置
- 比较器违反约定
- 遍历中修改Map
- ThreadLocal内存泄漏
- Integer缓存陷阱
- Date可变性
- 正则表达式性能问题
- serialVersionUID缺失
- 泛型类型擦除
- Lambda变量捕获
- equals/hashCode不一致
- 协变返回类型陷阱
- try-with-resources误用
- 数组与泛型不兼容
- finalize误用

### 3. ConcurrencyPitfalls.java
包含15个并发相关的陷阱：
- 非原子操作竞态条件
- volatile误用
- Double-Checked Locking问题
- wait/notify错误使用
- interrupt忽略
- CompletableFuture异常处理
- 线程池任务未等待
- ReentrantLock忘记unlock
- ConcurrentHashMap复合操作
- ThreadLocal在线程池中的问题
- CountDownLatch阻塞
- Semaphore permits泄露
- CyclicBarrier broken state
- ReadWriteLock升级死锁
- ForkJoinPool阻塞任务

## 测试目标

一个好的PR工具应该能够检测出以下问题：

### 严重级别（Critical）
- 空指针异常风险
- 资源泄漏
- 内存泄漏
- SQL注入漏洞
- 死锁风险
- 数据竞态条件

### 警告级别（Warning）
- 并发修改异常
- 异常被吞掉
- 性能问题（字符串拼接、正则表达式）
- 线程安全问题
- 锁使用不当

### 建议级别（Suggestion）
- 代码风格问题
- 最佳实践违背
- 潜在的逻辑错误
- 缺少null检查
- 不推荐使用的方法

## 预期检测结果

| 文件 | 应检测出的问题数 | 关键问题 |
|------|----------------|---------|
| CodePitfalls.java | 15+ | NPE, 资源泄漏, SQL注入, 死锁 |
| SubtlePitfalls.java | 20+ | 内存泄漏, 性能问题, API误用 |
| ConcurrencyPitfalls.java | 15+ | 竞态条件, 死锁, 线程安全 |

## 使用方法

将这些文件提交到PR中，观察工具是否能：
1. 准确识别所有陷阱
2. 给出正确的严重程度评级
3. 提供合理的修复建议
4. 不误报正常代码
5. 不漏报严重问题
