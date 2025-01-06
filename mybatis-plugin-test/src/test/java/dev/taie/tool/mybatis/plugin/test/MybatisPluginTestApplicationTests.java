package dev.taie.tool.mybatis.plugin.test;

import dev.taie.tool.mybatis.plugin.crypto.DesensitizedManager;
import dev.taie.tool.mybatis.plugin.crypto.service.impl.MybatisAutoCryptoServiceDefaultImpl;
import dev.taie.tool.mybatis.plugin.test.domain.Person;
import dev.taie.tool.mybatis.plugin.test.mapper.PersonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class MybatisPluginTestApplicationTests {
    @Autowired
    private PersonMapper personMapper;

    @Test
    void insert() {
        System.out.println(MybatisAutoCryptoServiceDefaultImpl.getKey());
        Person person = new Person().setName("a").setMobile("12345678");
        Person other = new Person().setName("b").setMobile("b1");
        Person child = new Person().setName("c").setMobile("c1");
        person.setChildren(Collections.singletonList(child)).setOther(other);
        List<Person> list = new ArrayList<>();
        list.add(person);
        Map<String, Person> map = new HashMap<>();
        map.put("person", person);
//        personMapper.insert3(person);
//        personMapper.insert1(person, "ZhangSan");
//        System.out.println("0---------------------------------------");
//        List<String> strings1 = personMapper.select1("123456");
//        System.out.println("1---------------------------------------");
//        List<String> strings = personMapper.select2("456789");
//        System.out.println("2---------------------------------------");
        List<Person> people = DesensitizedManager.execWithoutDesensitized(() -> personMapper.select3(), Person::getMobile);
        System.out.println(people);

//        personMapper.insert1(person, "ZhangSan");
        //       personMapper.insert(person, "ZhangSan");
//        personMapper.insert2("ZhangSan");
//        personMapper.insert3(person);
//        personMapper.insert4(list);
//        personMapper.insert5(map);
//        personMapper.insert6(1);
    }

}
