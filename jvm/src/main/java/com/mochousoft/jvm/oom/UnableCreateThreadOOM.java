package com.mochousoft.jvm.oom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 模拟复现 java.lang.OutOfMemoryError: unable to create new native thread.
 * 出现该异常主要有两个原因: 1、线程数超过操作系统限制, 2、内存中没有空间容纳新线程.
 * </p>
 * <br/>
 * <p>一、模拟线程数超过操作系统限制</p>
 * <p>1、cd /data</p>
 * <p>2、vim UnableCreateThreadOOM.java</p>
 * <p>3、将当前文件内容粘贴进去 (需要删除 package com.mochousoft.jvm.oom)</p>
 * <p>4、运行</p>
 * <p>&nbsp;&nbsp;java UnableCreateThreadOOM</p>
 * <p>5、运行结果</p>
 * <p>&nbsp;&nbsp;15510</p>
 * <p>&nbsp;&nbsp;15511</p>
 * <p>&nbsp;&nbsp;15512</p>
 * <p>&nbsp;&nbsp;15513</p>
 * <p>&nbsp;&nbsp;15514</p>
 * <p>&nbsp;&nbsp;Exception in thread "main" 15515</p>
 * <p>&nbsp;&nbsp;java.lang.OutOfMemoryError: unable to create new native thread</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at java.lang.Thread.start0(Native Method)</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at java.lang.Thread.start(Thread.java:717)</p>
 * <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at UnableCreateThreadOOM.main(UnableCreateThreadOOM.java:9)</p>
 * <font color='red'><p>&nbsp;&nbsp;Java HotSpot(TM) 64-Bit Server VM warning: Exception java.lang.</p>
 * <p>&nbsp;&nbsp;OutOfMemoryError occurred dispatching signal SIGINT to handler- </p>
 * <p>&nbsp;&nbsp;the VM may need to be forcibly terminated</p></font>
 * <p>6、结果分析</p>
 * <p>&nbsp;&nbsp;当前操作会导致服务器无法响应, 必须强制重启</p>
 * <p>&nbsp;&nbsp;通过 cat /proc/sys/kernel/threads-max 可以查看操作系统允许的最大线程数</p>
 * <br/>
 * <p>二、模拟内存中没有空间容纳新线程</p>
 * <p>1、</p>
 */
public class UnableCreateThreadOOM {

    private static final AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) {
        while (true) {
            (new TestThread1()).start();
            // (new TestThread2()).start();
        }
    }

    /**
     * 模拟线程数超过操作系统限制
     */
    public static class TestThread1 extends Thread {
        @Override
        public void run() {
            System.out.println(count.incrementAndGet());

            while (true) {
                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * 模拟内存中没有空间容纳新线程
     */
    public static class TestThread2 extends Thread {
        @Override
        public void run() {
            System.out.println(count.incrementAndGet());

            List<Object> list = new ArrayList<>();

            while (true) {
                try {
                    list.add(new Object());
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (Exception e) {
                    break;
                }
            }
        }
    }
}
