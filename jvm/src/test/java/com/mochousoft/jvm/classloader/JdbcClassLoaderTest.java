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
        // new MySQL8().query("select version()");

        System.out.println("=========================== 切换 ClassLoader");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
        }

        JdbcClassLoader jdbcClassLoader = new JdbcClassLoader(new URL[]{new File("D:\\Workspace\\Personal\\JavaTest\\jvm\\lib").toURI().toURL()}, classLoader);

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
