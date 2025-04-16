package dev.taie.tool.mybatis.plugin.crypto.config;

import dev.taie.tool.mybatis.plugin.crypto.interceptor.CryptoInterceptor;
import dev.taie.tool.mybatis.plugin.crypto.service.MybatisAutoCryptoService;
import dev.taie.tool.mybatis.plugin.crypto.service.impl.MybatisAutoCryptoServiceDefaultImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({CryptoProperties.class})
public class MybatisCryptoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MybatisAutoCryptoService.class)
    public MybatisAutoCryptoService mybatisAutoCryptoService(CryptoProperties cryptoProperties) {
        return new MybatisAutoCryptoServiceDefaultImpl(cryptoProperties);
    }

    @Bean
    public CryptoInterceptor cryptoInterceptor(MybatisAutoCryptoService mybatisAutoCryptoService, CryptoProperties cryptoProperties) {
        return new CryptoInterceptor(mybatisAutoCryptoService, cryptoProperties);
    }
}
