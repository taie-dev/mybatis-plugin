package dev.taie.tool.mybatis.plugin.crypto.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static dev.taie.tool.mybatis.plugin.crypto.config.CryptoProperties.CONF_PREFIX;

@ConfigurationProperties(CONF_PREFIX)
@Data
public class CryptoProperties {
    public static final String CONF_PREFIX = "mybatis.plugin.crypto";

    private String key;

    private String packageName;
}
