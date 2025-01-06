package dev.taie.tool.mybatis.plugin.test;

import dev.taie.tool.mybatis.plugin.crypto.annotation.CryptoField;
import dev.taie.tool.mybatis.plugin.crypto.service.MybatisAutoCryptoService;

/**
 * @date 2025/1/6
 */
//@Service
public class MybatisAutoCryptoServiceImpl implements MybatisAutoCryptoService {
    @Override
    public String encrypt(String plainText, CryptoField cryptoField) {
        return "encrypt";
    }

    @Override
    public String decrypt(String cipherText, CryptoField cryptoField) {
        return "decrypt";
    }
}
