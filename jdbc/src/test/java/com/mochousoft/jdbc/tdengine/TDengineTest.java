package com.mochousoft.jdbc.tdengine;

import com.mochousoft.jdbc.ClassLoaderSwapper;
import org.junit.jupiter.api.Test;

class TDengineTest {

    @Test
    public void testQuery() {
        ClassLoaderSwapper classLoaderSwapper = new ClassLoaderSwapper();
        classLoaderSwapper.setCurrentThreadClassLoader("lib/taos-jdbcdriver-2.0.40-dist.jar");

        TDengine tDengine = new TDengine();
        tDengine.queryByJdbc("select server_version()");
    }
}
