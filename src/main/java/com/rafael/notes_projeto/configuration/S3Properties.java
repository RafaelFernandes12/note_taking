package com.rafael.notes_projeto.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
    String endpoint,
    String region,
    String accessKey,
    String secretKey,
    String bucket) {
}
