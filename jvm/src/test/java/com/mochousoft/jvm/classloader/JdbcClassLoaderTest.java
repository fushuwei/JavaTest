package com.mochousoft.jvm.classloader;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

class JdbcClassLoaderTest {

    @Test
    public void switchMySQLVersion() throws Exception {
        System.out.println("=========================== 连接 MySQL 8");
        new MySQL8().query("select version()");

        System.out.println("=========================== 切换 ClassLoader");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
        }

        // 方式一、当前构造的ClassLoader的parent是AppClassLoader，而AppClassLoader中加载了MySQL8的驱动类，如果使用双亲委派机制加载MySQL5的驱动类，
        // 类加载器会首先加载当前ClassLoader的parent中的同名类，如果找不到才会在当前ClassLoader中查找，因此该方式加载的依旧是MySQL8版本的驱动类，因此直接这么使用是有问题的
        // 解决方案：重写自定义类加载器中的loadClass方法，且逻辑必须是先从当前ClassLoader中查找类，找不到再去递归parent中查找类，具体实现参考JdbcClassLoader类的loadClass()
        JdbcClassLoader jdbcClassLoader = new JdbcClassLoader(new URL[]{new File("D:\\Workspace\\Personal\\JavaTest\\jvm\\lib").toURI().toURL()});

        // 方式二、重置了当前构造的ClassLoader的parent，则当前ClassLoader及其parent都不包含AppClassLoader，因此可以直接使用双亲委派机制加载类。所以JdbcClassLoader可以不用重写loadClass方法
        // JdbcClassLoader jdbcClassLoader = new JdbcClassLoader(new URL[]{new File("D:\\Workspace\\Personal\\JavaTest\\jvm\\lib").toURI().toURL()}, classLoader);

        File file = new File("D:\\Workspace\\Personal\\JavaTest\\jvm\\lib");
        File[] array = file.listFiles();
        Optional.ofNullable(array).ifPresent(files -> {
            for (File tmp : array) {
                if (tmp.getName().endsWith(".jar")) {
                    try {
                        jdbcClassLoader.addFile(tmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread.currentThread().setContextClassLoader(jdbcClassLoader);

        System.out.println("ClassLoader切换完毕！");

        System.out.println("=========================== 连接 MySQL 5");

        new MySQL5().query("select version()");
    }
}
