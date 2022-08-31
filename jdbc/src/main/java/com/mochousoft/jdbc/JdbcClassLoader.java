package com.mochousoft.jdbc;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义类加载器
 */
public class JdbcClassLoader extends URLClassLoader {

    public JdbcClassLoader(String[] paths) {
        super(getURLs(paths));
    }

    public JdbcClassLoader(String[] paths, ClassLoader parent) {
        super(getURLs(paths), parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 先判断是否已经加载过
            Class<?> c = findLoadedClass(name);
            if (c != null) {
                if (resolve) {
                    resolveClass(c);
                }
                return c;
            }

            // 如果没有加载过，就自己找
            try {
                c = findClass(name);
                if (c != null) {
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }

            // 如果自己找不到，就根据双亲委派机制让parent去找
            try {
                if (getParent() != null) {
                    c = super.loadClass(name, resolve);
                    if (c != null) {
                        if (resolve) {
                            resolveClass(c);
                        }
                        return c;
                    }
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }

            // 如果parent也找不到，就让系统类加载器找
            try {
                c = findSystemClass(name);
                if (c != null) {
                    if (resolve) {
                        resolveClass(c);
                    }
                    return c;
                }
            } catch (ClassNotFoundException e) {
                // Ignore
            }

            // 以上都找不到，则抛异常
            throw new ClassNotFoundException(name);
        }
    }

    /**
     * 读取指定路径下的jar包
     *
     * @param paths jar包路径（数组类型）
     * @return 返回路径下所有jar包的URL（包含子目录）
     */
    public static URL[] getURLs(String[] paths) {
        List<URL> urls = new ArrayList<>();

        // 判断jar包路径是否为空
        if (paths == null || paths.length == 0) {
            return new URL[0];
        }

        // 循环所有jar包路径
        for (String path : paths) {
            // 判断当前路径是否为空
            if (path == null || "".equals(path)) {
                throw new RuntimeException("jar包路径不能为空");
            }

            // 创建当前路径的File对象
            File file = new File(path);

            // 判断当前路径是否存在
            if (!file.exists()) {
                throw new RuntimeException("jar包路径不存在：" + path);
            }

            // 判断当前路径是不是目录
            if (file.isDirectory()) {
                // 循环当前路径中的所有文件和目录
                for (File subFile : file.listFiles()) {
                    urls.addAll(Arrays.asList(getURLs(new String[]{subFile.getPath()})));
                }
            }

            // 如果当前路径是一个jar包文件，则记录该路径的URL
            try {
                if (file.isFile() && file.getName().endsWith(".jar")) {
                    urls.add(file.toURI().toURL());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return urls.toArray(new URL[0]);
    }
}
