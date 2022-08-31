package com.mochousoft.jdbc;

/**
 * ClassLoader交换器
 */
public final class ClassLoaderSwapper {

    private ClassLoader originalClassLoader;

    /**
     * 无参构造函数
     */
    public ClassLoaderSwapper() {
        // 保存当前线程的类加载器
        this.originalClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void setClassLoader(String filePath) {
        // 获取自定义类加载器的parent
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        while (classLoader.getParent() != null) {
            classLoader = classLoader.getParent();
        }

        // 实例化自定义类加载器
        JdbcClassLoader jdbcClassLoader = new JdbcClassLoader(new String[]{filePath}, classLoader);

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
