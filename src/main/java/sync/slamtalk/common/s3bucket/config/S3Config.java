package sync.slamtalk.common.s3bucket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/*
  S3client 에 accessKey, bucketName, region 주입
 */

@Configuration
public class S3Config {

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String accessSecret;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .credentialsProvider(this::awsCredentials)
                .region(Region.of(region))
                .build();
    }

    @Bean
    public AwsCredentials awsCredentials(){
        return new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return accessKey;
            }

            @Override
            public String secretAccessKey() {
                return accessSecret;
            }
        };
    }


}
