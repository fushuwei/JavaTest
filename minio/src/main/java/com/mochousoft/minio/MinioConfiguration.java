package com.mochousoft.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Minio 配置信息类
 *
 * @author fushuwei
 */
@Configuration
public class MinioConfiguration {

    @Autowired
    private MinioProperties minioProperties;

    @Bean
    public MinioClient getMinioClient() {
        return MinioClient.builder().endpoint(minioProperties.getUrl()).
            credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey()).build();
    }

}
