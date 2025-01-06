package dev.taie.tool.mybatis.plugin.test.domain;

import dev.taie.tool.mybatis.plugin.crypto.annotation.CryptoField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
public class Person implements Serializable {
    private static final long serialVersionUID = -9077828885837283912L;

    private Integer id;

    private String name;

    @CryptoField(desensitize = true, fieldName = "mobile")
    private String mobile;

    @CryptoField(fieldName = "longValue")
    private long longValue;

    private List<Person> children;

    private Person other;
}
