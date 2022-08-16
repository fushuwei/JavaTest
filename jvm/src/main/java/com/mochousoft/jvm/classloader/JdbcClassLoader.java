package com.mochousoft.jvm.classloader;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class JdbcClassLoader extends URLClassLoader {

    public JdbcClassLoader(String path) {
        super(getUrls(path));
    }

    public JdbcClassLoader(String[] paths) {
        super(getUrls(paths));
    }

    public JdbcClassLoader(String path, ClassLoader parent) {
        super(getUrls(path), parent);
    }

    public JdbcClassLoader(String[] paths, ClassLoader parent) {
        super(getUrls(paths), parent);
    }

    private static URL[] getUrls(String path) {
        return getUrls(new String[]{path});
    }

    private static URL[] getUrls(String[] paths) {
        // 校验path
        assert paths == null || paths.length <= 0 : "class 或 jar 路径不能为空";

        // todo 待改造

        List<String> dirs = new ArrayList<String>();
        for (String path : paths) {
            dirs.add(path);
            JdbcClassLoader.collectDirs(path, dirs);
        }

        List<URL> urls = new ArrayList<URL>();
        for (String path : dirs) {
            urls.addAll(doGetURLs(path));
        }

        return urls.toArray(new URL[0]);
    }

    private static void collectDirs(String path, List<String> collector) {
        if (null == path || "".equals(path)) {
            return;
        }

        File current = new File(path);
        if (!current.exists() || !current.isDirectory()) {
            return;
        }

        for (File child : current.listFiles()) {
            if (!child.isDirectory()) {
                continue;
            }

            collector.add(child.getAbsolutePath());
            collectDirs(child.getAbsolutePath(), collector);
        }
    }

    private static List<URL> doGetURLs(final String path) {
        assert path == null || "".equals(path) : "path不能为空";

        File jarPath = new File(path);

        assert jarPath.exists() && jarPath.isDirectory() : "jar包路径必须存在且为目录";

        /* set filter */
        FileFilter jarFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".jar");
            }
        };

        /* iterate all jar */
        File[] allJars = new File(path).listFiles(jarFilter);
        List<URL> jarURLs = new ArrayList<URL>(allJars.length);

        for (int i = 0; i < allJars.length; i++) {
            try {
                jarURLs.add(allJars[i].toURI().toURL());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return jarURLs;
    }
}
