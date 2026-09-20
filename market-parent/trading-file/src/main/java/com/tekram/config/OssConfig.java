package com.tekram.config;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.comm.SignVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssConfig {

    @Bean(destroyMethod = "shutdown")
    public OSS ossClient() throws Exception {

        ClientBuilderConfiguration config =
                new ClientBuilderConfiguration();

        config.setSignatureVersion(SignVersion.V4);

        return OSSClientBuilder.create()
                .endpoint("https://oss-cn-beijing.aliyuncs.com")
                .region("cn-beijing")
                .credentialsProvider(
                        CredentialsProviderFactory
                                .newEnvironmentVariableCredentialsProvider()
                )
                .clientConfiguration(config)
                .build();
    }
}