package dev.taie.tool.mybatis.plugin.crypto;

import dev.taie.tool.mybatis.plugin.crypto.util.LambdaGetter;
import dev.taie.tool.mybatis.plugin.crypto.util.LambdaUtil;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @date 2025/1/6
 */
public class DesensitizedManager {
    private static final ThreadLocal<Set<String>> skipFieldName = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> skipAllField = new ThreadLocal<>();

    @SafeVarargs
    public static <T, S> T execWithoutDesensitized(Supplier<T> supplier, LambdaGetter<S>... getters) {
        try {
            skipFieldName.set(Arrays.stream(getters).map(LambdaUtil::getFieldName).collect(Collectors.toSet()));
            return supplier.get();
        } finally {
            skipFieldName.remove();
        }
    }

    public static <T> T execWithoutDesensitized(Supplier<T> supplier, String... filedNames) {
        try {
            skipFieldName.set(Arrays.stream(filedNames).collect(Collectors.toSet()));
            return supplier.get();
        } finally {
            skipFieldName.remove();
        }
    }

    public static <T> T execWithoutDesensitized(Supplier<T> supplier) {
        try {
            skipAllField.set(Boolean.TRUE);
            return supplier.get();
        } finally {
            skipAllField.remove();
        }
    }

    public static boolean isSkipDesensitized(String filedName) {
        Boolean skipAll = skipAllField.get();
        if (skipAll != null && skipAll) {
            return true;
        }
        Set<String> set = skipFieldName.get();
        if (set == null) {
            return false;
        }
        return set.contains(filedName);
    }

}
