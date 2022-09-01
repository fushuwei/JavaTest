package com.mochousoft.jdbc.tdengine;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * 涛思数据库
 * <br/>
 * 技术文档: https://docs.taosdata.com
 */
public class TDengine {

    /**
     * JDBC方式连接，生产环境可能需要将 libtaos.so 文件放到服务器的 /usr/lib/ 目录中
     */
    public void queryByJdbc(String sql) {
        try {
            // 加载驱动
            Driver driver = (Driver) Thread.currentThread().getContextClassLoader().loadClass("com.taosdata.jdbc.TSDBDriver").newInstance();
            // 获取连接
            Properties props = new Properties();
            props.setProperty("user", "root");
            props.setProperty("password", "taosdata");
            Connection conn = driver.connect("jdbc:TAOS://h1.taosdata.com:6030/log?timezone=UTC-8&charset=UTF-8&locale=en_US.UTF-8", props);

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

    /**
     * REST方式连接，该方式性能较差，不推荐使用
     */
    public void queryByRest(String sql) {
        try {
            // 加载驱动
            Driver driver = (Driver) Thread.currentThread().getContextClassLoader().loadClass("com.taosdata.jdbc.rs.RestfulDriver").newInstance();
            // 获取连接
            Properties props = new Properties();
            props.setProperty("user", "root");
            props.setProperty("password", "taosdata");
            Connection conn = driver.connect("jdbc:TAOS-RS://h1.taosdata.com:6041/log?timezone=UTC-8&charset=UTF-8&locale=en_US.UTF-8", props);

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
