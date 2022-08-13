package com.mochousoft.thread.classloader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MySQL8 {

    public void query1(String sql) throws Exception {
        // 加载驱动
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 获取连接
        Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.169.128:3308/test?characterEncoding=utf8", "root", "123456");

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
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 获取连接
        Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.169.128:3308/test?characterEncoding=utf8", "root", "123456");

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
