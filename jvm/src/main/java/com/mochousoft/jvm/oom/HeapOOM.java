package com.mochousoft.jvm.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>模拟堆内存溢出</p>
 * <br/>
 * <p><b><font color='red'>什么时候会出现堆OOM</font></b></p>
 * <p>1. 不断的在堆中创建对象</p>
 * <p>2. 垃圾回收机制无法回收对象</p>
 * <br/>
 * <p>准备条件</p>
 * <p>1. 设置JAVA虚拟机内存大小: -Xms3m -Xmx3m</p>
 * <p>2. 设置堆转储 (.hprof 文件): -XX:+HeapDumpOnOutOfMemoryError</p>
 */
public class HeapOOM {

    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();

        while (true) {
            list.add(new Object());
        }
    }
}
