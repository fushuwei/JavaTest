package com.mochousoft.minio;

import com.mochousoft.minio.jdbc.ClassLoaderSwapper;
import com.mochousoft.minio.jdbc.MySQL8;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
class MinioApplicationTests {

    @Autowired
    private MinioService minioService;

    @Test
    void contextLoads() {
    }

    @Test
    void testMinioConnection() {
        try {
            List<String> bucketList = minioService.listBuckets();

            bucketList.forEach(System.out::println);

            for (String bucket : bucketList) {

                List<FileInfo> fileInfoList = minioService.listFiles(bucket);

                fileInfoList.forEach(System.out::println);

                if ("jdbc-driver".equals(bucket)) {
                    File f = new File("D:\\Workspace\\Personal\\JavaTest\\jdbc\\lib\\mysql-connector-java-8.0.29.jar");
                    InputStream is = new FileInputStream(f);
                    minioService.uploadFile(is, bucket, f.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMySQLConnectionByMinio() {
        try {
            // 下载jar
            InputStream is = minioService.download("jdbc-driver", "mysql-connector-java-8.0.29.jar");

            ClassLoaderSwapper classLoaderSwapper = new ClassLoaderSwapper();
            classLoaderSwapper.setCurrentThreadClassLoader("mysql-connector-java-8.0.29.jar", is);

            MySQL8 mysql8 = new MySQL8();
            mysql8.query("select version()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
