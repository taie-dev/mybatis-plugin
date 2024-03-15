package dev.taie.tool.mybatis.plugin.test;

import dev.taie.tool.mybatis.plugin.test.domain.Person;
import dev.taie.tool.mybatis.plugin.test.mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MybatisPluginTestApplicationTests {
    @Autowired
    private PersonMapper personMapper;

    @Test
    void insert() {
        Person person = new Person().setName("a").setMobile("12345678");
        personMapper.insert(person);
    }

}
