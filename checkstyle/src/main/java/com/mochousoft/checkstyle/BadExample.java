package com.mochousoft.checkstyle;


public class BadExample { // 1、没有类的 Javadoc，2、有两个空行

    private static final String var_constant = "BadExample"; // 常量命名不规范

    private final static String VAR_CONSTANT = "BadExample"; // static 和 final 的顺序错误

    private static String var_static; // 静态变量命名不规范

    String name;

    public int getAge() { // 符合规范，CheckStyle 将其识别为 getter setter 方法
        return 18;
    }

    public int getage() { // 是普通方法，但是没有方法的 Javadoc
        return 18;
    }

    @Override
    public String toString() { // 因为使用了 @Override 注解，所以可以没有方法的 Javadoc
        return super.toString();
    }

    /**
     *
     */
    public int test(int a, int b) { // 方法的 Javadoc 中允许没有 @param 和 @return
        // @formatter:off
        int c = a +b; // + 号后面没有空格
        // @formatter:on

        try {
            System.out.println(c);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
