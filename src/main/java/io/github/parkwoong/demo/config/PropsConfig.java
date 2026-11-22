package io.github.parkwoong.demo.config;

import io.github.parkwoong.demo.properties.GatewayProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class PropsConfig {}
