package com.d2d.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MinioConfig {
    /*

    @Param minioUrl
    @Description Constant data read from application.yml file.

    */
    @Value("${minio.url}")
    private String minioUrl;

    /*

    @Param accessKey
    @Description Constant data read from application.yml file.

    */
    @Value("${minio.accessKey}")
    private String accessKey;

    /*
    @Param secretKey
    @Description Constant data read from application.yml file.
    */
    @Value("${minio.secretKey}")
    private String secretKey;

    /*
    @Param secretKey
    @Description Constant data read from application.yml file.
    */
    @Value("${minio.region}")
    private String region;


    /*
    @Bean minioClient
    @Description Set up minio server connection in java.
    */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .region(region)
                .credentials(accessKey, secretKey)
                .build();
    }
}

