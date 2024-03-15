package dev.taie.tool.mybatis.plugin.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("dev.taie.tool.mybatis.plugin.test.mapper")
public class MybatisPluginTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPluginTestApplication.class, args);
    }

}
