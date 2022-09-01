package com.mochousoft.jdbc.mysql;

import com.mochousoft.jdbc.ClassLoaderSwapper;
import org.junit.jupiter.api.Test;

class MySQL5Test {

    @Test
    public void testQuery() {
        ClassLoaderSwapper classLoaderSwapper = new ClassLoaderSwapper();
        classLoaderSwapper.setCurrentThreadClassLoader("lib/mysql-connector-java-5.1.34.jar");

        MySQL5 mysql5 = new MySQL5();
        mysql5.query("select version()");
    }
}
