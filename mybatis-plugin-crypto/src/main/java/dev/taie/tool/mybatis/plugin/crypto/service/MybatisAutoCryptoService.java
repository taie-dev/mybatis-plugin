package dev.taie.tool.mybatis.plugin.crypto.service;

import dev.taie.tool.mybatis.plugin.crypto.annotation.CryptoField;

/**
 * @date 2025/1/6
 */
public interface MybatisAutoCryptoService {
    String encrypt(String plainText, CryptoField cryptoField);

    String decrypt(String cipherText, CryptoField cryptoField);
}
