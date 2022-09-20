package com.mochousoft.jvm.oom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>该类用于模拟复现 java.lang.OutOfMemoryError: unable to create new native thread.</p>
 * <br/>
 * <p>出现该异常主要有两个原因:</p>
 * <p>1、线程数超过操作系统限制</p>
 * <p>2、内存中没有空间容纳新线程</p>
 * <br/>
 * <p>一、模拟步骤: </p>
 * <p>1、cd /data</p>
 * <p>2、vim UnableCreateThreadOOM.java</p>
 * <p>3、将当前文件内容粘贴进去 (需要删除 package com.mochousoft.jvm.oom)</p>
 * <p>4、运行测试程序</p>
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
 * <p>&nbsp;&nbsp;the VM may need to be forcibly terminated</p>
 * <p>&nbsp;&nbsp;(注意: 当前操作会导致服务器无法响应, 需要重启机器)</p></font>
 * <br/>
 * <p>二、解决办法</p>
 * <p>1、通过 cat /proc/sys/kernel/threads-max 可以查看操作系统允许的最大线程数</p>
 * <p>2、修改最大线程数: echo 50000 > /proc/sys/kernel/threads-max</p>
 * <p>3、运行 java UnableCreateThreadOOM 验证效果</p>
 * <p>4、运行结果</p>
 * <p>&nbsp;&nbsp;32700</p>
 * <p>&nbsp;&nbsp;32701</p>
 * <p>&nbsp;&nbsp;32702</p>
 * <p>&nbsp;&nbsp;32703</p>
 * <p>&nbsp;&nbsp;32704</p>
 * <p>&nbsp;&nbsp;32705</p>
 * <p>&nbsp;&nbsp;32706</p>
 * <p>&nbsp;&nbsp;Java HotSpot(TM) 64-Bit Server VM warning: Attempt to protect stack guard pages failed.</p>
 * <p>&nbsp;&nbsp;Java HotSpot(TM) 64-Bit Server VM warning: Attempt to deallocate stack guard pages failed.</p>
 * <p>&nbsp;&nbsp;32707</p>
 * <p>&nbsp;&nbsp;Exception in thread "main" Java HotSpot(TM) 64-Bit Server VM warning: INFO: os::commit_memory(0x00007f229a1a5000, 12288, 0) failed; error='无法分配内存' (errno=12)</p>
 * <p>&nbsp;&nbsp;#</p>
 * <p>&nbsp;&nbsp;# There is insufficient memory for the Java Runtime Environment to continue.</p>
 * <p>&nbsp;&nbsp;# Native memory allocation (mmap) failed to map 12288 bytes for committing reserved memory.</p>
 * <p>&nbsp;&nbsp;# An error report file with more information is saved as:</p>
 * <p>&nbsp;&nbsp;# /data/hs_err_pid1483.log</p>
 * <p>&nbsp;&nbsp;java.lang.OutOfMemoryError: unable to create new native thread</p>
 * <p>&nbsp;&nbsp;	at java.lang.Thread.start0(Native Method)</p>
 * <p>&nbsp;&nbsp;	at java.lang.Thread.start(Thread.java:717)</p>
 * <p>&nbsp;&nbsp;	at UnableCreateThreadOOM.main(UnableCreateThreadOOM.java:9)</p>
 * <p>&nbsp;&nbsp;Java HotSpot(TM) 64-Bit Server VM warning: Attempt to deallocate stack guard pages failed.</p>
 * <p>&nbsp;&nbsp;Java HotSpot(TM) 64-Bit Server VM warning: INFO: os::commit_memory(0x00007f2ae82b3000, 12288, 0) failed; error='无法分配内存' (errno=12)</p>
 * <p>&nbsp;&nbsp;[thread 139822261544704 also had an error]</p>
 * <p>5、结果分析</p>
 * <p><font color='red'>&nbsp;&nbsp;将最大线程数设置成50000后，重新运行程序确实比修改之前多创建了很多线程，但是
 * 尚未创建到50000个线程就报错了，是因为没有足够的内存分配给新创建的线程使用</font></p>
 * <br/>
 */
public class UnableCreateThreadOOM {

    private static final AtomicInteger count = new AtomicInteger();

    public static void main(String[] args) {
        while (true) {
            (new TestThread()).start();
        }
    }

    /**
     * 模拟线程数超过操作系统限制
     */
    public static class TestThread extends Thread {

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
}
