package com.mochousoft.minio.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class MySQL8 {

    public void query(String sql) {
        try {
            // 加载驱动
            // Class.forName("com.mysql.cj.jdbc.Driver");
            // 获取连接
            // Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.169.128:3308/test?characterEncoding=utf8", "root", "123456");

            // 加载驱动
            Driver driver = (Driver) Thread.currentThread().getContextClassLoader().loadClass("com.mysql.cj.jdbc.Driver").newInstance();
            // 获取连接
            Properties props = new Properties();
            props.setProperty("user", "root");
            props.setProperty("password", "123456");
            Connection conn = driver.connect("jdbc:mysql://192.168.169.128:3308/test?characterEncoding=utf8", props);

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
