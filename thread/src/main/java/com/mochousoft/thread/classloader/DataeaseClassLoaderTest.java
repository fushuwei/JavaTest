package com.mochousoft.thread.classloader;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Component
public class DataeaseClassLoaderTest {

    @Bean
    public void testClassMethod() throws Exception {
        System.out.println("=========================== 连接 MySQL 8");
        try {
            new MySQL8().query1("select version()");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=========================== 切换 ClassLoader");

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
        }

        DataeaseClassLoader dataeaseClassLoader = new DataeaseClassLoader(new URL[]{new File("D:\\Workspace\\Personal\\JavaTest\\thread\\lib").toURI().toURL()});
        // DataeaseClassLoader dataeaseClassLoader = new DataeaseClassLoader(new URL[]{new File("D:\\Workspace\\Personal\\JavaTest\\thread\\lib").toURI().toURL()}, classLoader);

        File file = new File("D:\\Workspace\\Personal\\JavaTest\\thread\\lib");
        File[] array = file.listFiles();
        Optional.ofNullable(array).ifPresent(files -> {
            for (File tmp : array) {
                if (tmp.getName().endsWith(".jar")) {
                    try {
                        dataeaseClassLoader.addFile(tmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread.currentThread().setContextClassLoader(dataeaseClassLoader);

        System.out.println("切换完毕！");

        System.out.println("=========================== 连接 MySQL 5");
        try {

            // 测试 classloader 是否生效, 因为指定路径下存在 postgresql 的驱动, 因此下面一行代码正确
            // Thread.currentThread().getContextClassLoader().loadClass("org.postgresql.Driver").newInstance();

            // 由于 类.class.getClassLoader() 与 Thread.currentThread().getContextClassLoader() 不同, 因此下面一行代码报错
            // Method threw 'java.lang.ClassNotFoundException' exception.
            // MySQL5.class.getClassLoader().loadClass("org.postgresql.Driver").newInstance();

            new MySQL5().query3("select version()");
            // MySQL5.query2("select version()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Bean
    public void testStaticMethod() {
        System.out.println("=========================== 连接 MySQL 8");
        try {
            MySQL8.query2("select version()");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("=========================== 切换 ClassLoader");

        // 继承切换前的classloader方式
        // Thread.currentThread().setContextClassLoader(new JdbcClassLoader("D:\\Workspace\\Personal\\JavaTest\\thread\\lib"));

        // 不继承切换前的classloader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
        }
        Thread.currentThread().setContextClassLoader(new JdbcClassLoader("D:\\Workspace\\Personal\\JavaTest\\thread\\lib", classLoader));

        System.out.println("切换完毕！");

        System.out.println("=========================== 连接 MySQL 5");
        try {
            MySQL5.query2("select version()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
