package com.mochousoft.thread.classloader;

import java.sql.*;
import java.util.Properties;

public class MySQL5 {

    public void query1(String sql) throws Exception {
        // 加载驱动
        Class.forName("com.mysql.jdbc.Driver");

        // 获取连接
        Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.169.128:3301/test?characterEncoding=utf8", "root", "123456");

        // 查询sql
        PreparedStatement ps = conn.prepareStatement(sql);

        // 执行查询
        ResultSet rs = ps.executeQuery();

        // 获取结果集
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }

        // 关闭连接
        rs.close();
        ps.close();
        conn.close();
    }

    public static void query2(String sql) throws Exception {
        // 加载驱动
        Driver driver = (Driver) Thread.currentThread().getContextClassLoader().loadClass("com.mysql.jdbc.Driver").newInstance();

        // 获取连接
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "123456");
        Connection conn = driver.connect("jdbc:mysql://192.168.169.128:3301/test?characterEncoding=utf8", props);

        // 查询sql
        PreparedStatement ps = conn.prepareStatement(sql);

        // 执行查询
        ResultSet rs = ps.executeQuery();

        // 获取结果集
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }

        // 关闭连接
        rs.close();
        ps.close();
        conn.close();
    }

    public void query3(String sql) throws Exception {
        // 加载驱动
        Driver driver = (Driver) Thread.currentThread().getContextClassLoader().loadClass("com.mysql.jdbc.Driver").newInstance();

        // 获取连接
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "123456");
        Connection conn = driver.connect("jdbc:mysql://192.168.169.128:3301/test?characterEncoding=utf8", props);

        // 查询sql
        PreparedStatement ps = conn.prepareStatement(sql);

        // 执行查询
        ResultSet rs = ps.executeQuery();

        // 获取结果集
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }

        // 关闭连接
        rs.close();
        ps.close();
        conn.close();
    }
}
