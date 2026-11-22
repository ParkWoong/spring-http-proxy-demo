package io.github.parkwoong.demo.properties;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("gateway")
public class GatewayProperties {

    private String origin;
    private String tokenType;
}
