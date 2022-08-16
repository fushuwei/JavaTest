package com.mochousoft.jvm.classloader;

// @Component
public class JdbcClassLoaderTest {

    // @Bean
    public void testClassMethod() {
        System.out.println("=========================== 连接 MySQL 8");
        try {
            new MySQL8().query1("select version()");
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
            new MySQL5().query1("select version()");
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
