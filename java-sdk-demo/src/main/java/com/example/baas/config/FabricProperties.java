package com.example.baas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author John
 * @create 2019/7/9 14:25
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "fabric")
public class FabricProperties{

    private Integer waitTime = 6000;
    private String connectionProfilePath = "connection-profile.json";
    private String channelName;
    private String userName;
    private String secret;
    private String chaincodeName;
    private String chaincodeVersion;
}
