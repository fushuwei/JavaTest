package com.mochousoft.jvm.oom;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>模拟超出GC开销限制内存溢出</p>
 * <br/>
 * <p>准备条件</p>
 * <p>1. 设置JAVA虚拟机内存大小: -Xms3m -Xmx3m</p>
 */
public class GCOverheadLimitOOM {

    public static void main(String[] args) {
        Map<Integer, Object> map = new HashMap<>();

        int i = 0;
        while (true) {
            Object object = new Object();
            map.put(i++, object);
        }
    }
}
