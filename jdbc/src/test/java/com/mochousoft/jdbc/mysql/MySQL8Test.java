package com.mochousoft.jdbc.mysql;

import com.mochousoft.jdbc.ClassLoaderSwapper;
import org.junit.jupiter.api.Test;

class MySQL8Test {

    @Test
    public void testQuery() {
        ClassLoaderSwapper classLoaderSwapper = new ClassLoaderSwapper();
        classLoaderSwapper.setClassLoader("lib/mysql-connector-java-8.0.29.jar");

        MySQL8 mysql8 = new MySQL8();
        mysql8.query("select version()");
    }
}
