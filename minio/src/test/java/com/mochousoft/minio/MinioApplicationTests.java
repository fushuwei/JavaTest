package com.mochousoft.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            if (true) {
                return;
            }


            // 连接Minio
            MinioClient minioClient = MinioClient.builder().endpoint("http://192.168.7.250:9000").credentials("minioadmin", "minioadmin").build();

            // Make 'asiatrip' bucket if not exist.
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("abc").build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("abc").build());
            } else {
                System.out.println("Bucket 'abc' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name
            // 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
//			minioClient.uploadObject(UploadObjectArgs.builder().bucket("abc").object("1.txt")
//					.filename("D:\\实时监控数据.txt").build());
//			System.out.println("'实时监控数据.txt' is successfully uploaded as "
//					+ "object '1.txt' to bucket 'abc'.");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
