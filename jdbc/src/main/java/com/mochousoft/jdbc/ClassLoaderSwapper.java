package com.mochousoft.jdbc;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * ClassLoader交换器
 */
public final class ClassLoaderSwapper {

    private ClassLoader originalClassLoader;

    public ClassLoaderSwapper() {
        // 保存当前线程的类加载器
        this.originalClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void setClassLoader(String classpath) {
        // 获取自定义类加载器的parent
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
        }

        // 实例化自定义类加载器
        JdbcClassLoader jdbcClassLoader = null;
        try {
            jdbcClassLoader = new JdbcClassLoader(new URL[]{new File(classpath).toURI().toURL()}, classLoader);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // File file = new File(classpath);
        // File[] array = file.listFiles();
        // Optional.ofNullable(array).ifPresent(files -> {
        //     for (File tmp : array) {
        //         if (tmp.getName().endsWith(".jar")) {
        //             try {
        //                 jdbcClassLoader.addFile(tmp);
        //             } catch (IOException e) {
        //                 e.printStackTrace();
        //             }
        //         }
        //     }
        // });

        this.setClassLoader(jdbcClassLoader);
    }

    /**
     * 为当前线程设置新的类加载器
     *
     * @param classLoader 自定义类加载器
     */
    public void setClassLoader(ClassLoader classLoader) {
        Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * 还原当前线程的类加载器
     */
    public void restoreClassLoader() {
        Thread.currentThread().setContextClassLoader(this.originalClassLoader);
    }

    /**
     * 获取当前线程原始的类加载器
     *
     * @return 返回当前线程原始的类加载器
     */
    public ClassLoader getOriginalClassLoader() {
        return originalClassLoader;
    }
}
