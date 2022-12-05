package com.alok.mqtt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Profile;

@Data
public class Properties {

    private Mqtt mqtt;
    private Secure secure;

    @Data
    public static class Mqtt {
        private String host;
        private String port;
        private Boolean cleanState;
        private Boolean autoReconnect;
        private Integer keepAlive;
        private Integer connectionRetry;
        private Integer connectionTimeout;

        private String clientId;
        private Integer publishQos;
        private String publishTopic;

        private Integer subscribeQos;
        private String subscribeTopic;

        private String statusTopic;
    }

    @Data
    public static class Secure {

        private String keystoreType;

        private FilePathType keystoreFilePathType;
        private String keystoreFile;
        private String keystorePassword;

        private FilePathType truststoreFilePathType;
        private String truststoreFile;
        private String truststorePassword;

    }

    public enum FilePathType {
        ABSOLUTE,
        RESOURCE
    }
}
