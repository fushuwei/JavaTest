package com.mochousoft.minio;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Minio 参数配置类
 *
 * @author fushuwei
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    // @Value("${minio.url}")
    private String url;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;
}
