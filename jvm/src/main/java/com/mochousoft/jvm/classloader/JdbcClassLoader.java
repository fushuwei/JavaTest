package com.mochousoft.jvm.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class JdbcClassLoader extends URLClassLoader {

    public JdbcClassLoader(URL[] urls) {
        super(urls);
    }

    public JdbcClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name, false);
    }

    // @Override
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

    public void addFile(String s) throws IOException {
        File f = new File(s);
        addFile(f);
    }

    public void addFile(File f) throws IOException {
        addFile(f.toURI().toURL());
    }

    public void addFile(URL u) throws IOException {
        try {
            this.addURL(u);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }
}
