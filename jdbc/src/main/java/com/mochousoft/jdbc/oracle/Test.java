package com.mochousoft.jdbc.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Test {

    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");

            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@//218.194.177.70:1521/JWDB", "cqwu_jwjk", "Cqwu_jwjk123");
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM CQWU_JWGL.T_XS_STUDENT");

            // 调整该参数可以模拟Oracle数据预加载至内存的数据大小，超出缓冲值则会抛出 connection reset 异常
            ps.setFetchSize(1024);
            ps.setQueryTimeout(172800);

            ResultSet rs = ps.executeQuery();

            int x = 1;
            while (rs.next()) {
                System.out.println("##################################################################### " + x);
                StringBuilder rowData = new StringBuilder();
                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    rowData.append(rs.getString(i + 1)).append("   ");
                }
                System.out.println(rowData + "\n\n");
                x++;
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
