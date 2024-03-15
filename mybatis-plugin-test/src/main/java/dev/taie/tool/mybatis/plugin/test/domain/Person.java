package dev.taie.tool.mybatis.plugin.test.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class Person implements Serializable {
    private static final long serialVersionUID = -9077828885837283912L;

    private Integer id;

    private String name;

    private String mobile;
}
