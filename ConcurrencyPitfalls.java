package com.test.pitfalls;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发相关的陷阱测试类
 */
public class ConcurrencyPitfalls {
    
    private int counter = 0;
    private AtomicInteger atomicCounter = new AtomicInteger(0);
    
    /**
     * 坑1: 非原子操作的竞态条件
     */
    public void increment() {
        // 这不是原子操作！多线程环境下会丢失更新
        counter++;
        // 等价于：int temp = counter; counter = temp + 1;
    }
    
    /**
     * 坑2: volatile的误用 - 复合操作不是原子的
     */
    private volatile int volatileCounter = 0;
    
    public void incrementVolatile() {
        // volatile只保证可见性，不保证原子性
        volatileCounter++; // 仍然有竞态条件！
    }
    
    /**
     * 坑3: Double-Checked Locking的错误实现
     */
    private static ExpensiveObject instance;
    
    public static ExpensiveObject getInstance() {
        if (instance == null) {
            synchronized (ConcurrencyPitfalls.class) {
                if (instance == null) {
                    // 在Java 5之前有问题，对象可能在完全初始化前被其他线程看到
                    instance = new ExpensiveObject();
                }
            }
        }
        return instance;
    }
    
    /**
     * 坑4: wait/notify的错误使用
     */
    private final Object monitor = new Object();
    private boolean condition = false;
    
    public void waitForCondition() throws InterruptedException {
        synchronized (monitor) {
            // 错误：没有检查条件就直接wait
            // 应该用while循环而不是if
            if (!condition) {
                monitor.wait(); // 可能被虚假唤醒！
            }
        }
    }
    
    public void signalCondition() {
        synchronized (monitor) {
            condition = true;
            monitor.notify(); // 应该用notifyAll()更安全
        }
    }
    
    /**
     * 坑5: Thread.interrupt()的忽略
     */
    public void longRunningTask() {
        while (true) {
            // 没有检查中断状态
            doSomeWork();
            
            // 应该检查：if (Thread.currentThread().isInterrupted()) break;
        }
    }
    
    private void doSomeWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // 吞掉了中断异常，没有恢复中断状态
            // 应该：Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 坑6: CompletableFuture的异常处理缺失
     */
    public void testCompletableFuture() {
        CompletableFuture.supplyAsync(() -> {
            // 如果这里抛出异常...
            if (Math.random() > 0.5) {
                throw new RuntimeException("Error!");
            }
            return "result";
        }).thenApply(result -> {
            // ...这里的代码不会执行，异常会被吞掉
            return result.toUpperCase();
        });
        // 没有exceptionally()或handle()来处理异常
    }
    
    /**
     * 坑7: 线程池任务提交后未等待完成
     */
    public void submitAndForget() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                // 提交任务但不等待结果
                System.out.println("Task executed");
            });
        }
        
        // 立即关闭，可能导致任务未完成就被终止
        executor.shutdownNow();
    }
    
    /**
     * 坑8: ReentrantLock忘记unlock
     */
    private final ReentrantLock lock = new ReentrantLock();
    
    public void unsafeLockUsage() {
        lock.lock();
        try {
            // 如果这里抛出异常...
            doCriticalWork();
        } finally {
            // unlock应该在finally块中
            lock.unlock();
        }
    }
    
    private void doCriticalWork() {
        // critical section
    }
    
    /**
     * 坑9: ConcurrentHashMap的复合操作
     */
    private ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
    
    public void updateConcurrentMap(String key) {
        // get和put不是原子操作，即使使用ConcurrentHashMap
        Integer value = concurrentMap.get(key);
        if (value == null) {
            value = 0;
        }
        concurrentMap.put(key, value + 1);
        
        // 应该用：concurrentMap.merge(key, 1, Integer::sum);
    }
    
    /**
     * 坑10: ThreadLocal在线程池中的问题
     */
    private static ThreadLocal<SimpleDateFormat> dateFormatHolder = ThreadLocal.withInitial(
        () -> new SimpleDateFormat("yyyy-MM-dd")
    );
    
    public String formatDate(Date date) {
        // SimpleDateFormat不是线程安全的
        // 虽然用了ThreadLocal，但在线程池中可能导致内存泄漏
        return dateFormatHolder.get().format(date);
    }
    
    /**
     * 坑11: CountDownLatch的误用
     */
    public void testCountDownLatch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    doWork();
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        // 如果某个线程一直不countDown，这里会永久阻塞
        latch.await(); // 应该设置超时：latch.await(5, TimeUnit.SECONDS)
    }
    
    private void doWork() {
        // simulate work
    }
    
    /**
     * 坑12: Semaphore permits泄露
     */
    private Semaphore semaphore = new Semaphore(5);
    
    public void acquireResource() throws InterruptedException {
        semaphore.acquire();
        try {
            useResource();
        } finally {
            // 如果忘记release，permits会逐渐耗尽
            semaphore.release();
        }
    }
    
    private void useResource() {
        // use resource
    }
    
    /**
     * 坑13: CyclicBarrier的broken state
     */
    public void testCyclicBarrier() {
        CyclicBarrier barrier = new CyclicBarrier(3);
        
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    // 如果一个线程失败，barrier会进入broken状态
                    // 其他线程会抛出BrokenBarrierException
                }
            }).start();
        }
    }
    
    /**
     * 坑14: ReadWriteLock的升级死锁
     */
    private ReadWriteLock rwLock = new ReentrantReadWriteLock();
    
    public void lockUpgradeDeadlock() {
        rwLock.readLock().lock();
        try {
            // 尝试获取写锁会导致死锁
            // ReentrantReadWriteLock不支持锁升级
            rwLock.writeLock().lock(); // 永远阻塞！
            try {
                // write operation
            } finally {
                rwLock.writeLock().unlock();
            }
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    /**
     * 坑15: ForkJoinPool的阻塞任务
     */
    public void blockingInForkJoin() {
        ForkJoinPool pool = new ForkJoinPool(4);
        
        // ForkJoinPool适合CPU密集型任务，不适合IO阻塞任务
        pool.submit(() -> {
            try {
                // 阻塞操作会降低整个pool的性能
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
    
    static class ExpensiveObject {
        public ExpensiveObject() {
            // expensive initialization
        }
    }
}
