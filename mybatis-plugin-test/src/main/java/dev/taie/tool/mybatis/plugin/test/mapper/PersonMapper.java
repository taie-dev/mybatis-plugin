package dev.taie.tool.mybatis.plugin.test.mapper;

import dev.taie.tool.mybatis.plugin.test.domain.Person;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PersonMapper {
    int insert1(@Param("person") Person person,@Param("str") String str);

    void insert2(String zhangSan);

    void insert3(Person person);

    void insert4(@Param("personList") List<Person> list);

    void insert5(@Param("personMap") Map<String, Person> map);

    void insert6(@Param("num") int a);

    List<String> select1(@Param("num") String a);

    List<String> select2(String a);

    List<Person> select3();
}
