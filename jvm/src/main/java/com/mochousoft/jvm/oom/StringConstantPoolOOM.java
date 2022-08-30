package com.mochousoft.jvm.oom;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>模拟字符串常量池内存溢出</p>
 * <br/>
 * <p>准备条件</p>
 * <p>1. 设置JAVA虚拟机内存大小: -Xms3m -Xmx3m</p>
 */
public class StringConstantPoolOOM {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

        while (true) {
            list.add(UUID.randomUUID().toString().intern());
        }
    }
}
