package dev.taie.tool.mybatis.plugin.crypto.service.impl;

import dev.taie.tool.mybatis.plugin.crypto.DesensitizedManager;
import dev.taie.tool.mybatis.plugin.crypto.annotation.CryptoField;
import dev.taie.tool.mybatis.plugin.crypto.config.CryptoProperties;
import dev.taie.tool.mybatis.plugin.crypto.exception.CryptoException;
import dev.taie.tool.mybatis.plugin.crypto.service.MybatisAutoCryptoService;
import dev.taie.tool.mybatis.plugin.crypto.util.AesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @date 2025/1/6
 */
public class MybatisAutoCryptoServiceDefaultImpl implements MybatisAutoCryptoService {
    private static final Logger log = LoggerFactory.getLogger(MybatisAutoCryptoServiceDefaultImpl.class);

    private final CryptoProperties cryptoProperties;

    public MybatisAutoCryptoServiceDefaultImpl(CryptoProperties cryptoProperties) {
        this.cryptoProperties = cryptoProperties;
    }

    @Override
    public String encrypt(String plainText, CryptoField cryptoField) {
        if (plainText == null) {
            return null;
        }
        // 如果包含脱敏字符，将值置空
        if (plainText.contains(cryptoField.desensitizationMake())) {
            return null;
        }
        String res;
        try {
            res = encrypt(plainText);
        } catch (Exception e) {
            log.error("自动加密失败");
            throw new CryptoException("自动加密失败", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("加密结果:{}->{}", desensitize(plainText), res);
        }
        return res;
    }

    @Override
    public String decrypt(String cipherText, CryptoField cryptoField) {
        if (cipherText == null) {
            return null;
        }
        String res;
        boolean isDesensitize;
        try {
            res = decrypt(cipherText);
            isDesensitize = cryptoField.desensitize() && !DesensitizedManager.isSkipDesensitized(cryptoField.fieldName());
            if (isDesensitize) {
                res = desensitize(res);
            }
        } catch (Exception e) {
            log.error("自动解密失败");
            throw new CryptoException("自动解密失败", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("解密结果:{}->{}", cipherText, isDesensitize ? res : desensitize(res));
        }
        return res;
    }

    private String encrypt(String plainText) throws Exception {
        return AesUtils.encrypt(plainText, cryptoProperties.getKey());
    }

    private String decrypt(String encryptedText) throws Exception {
        return AesUtils.decrypt(encryptedText, cryptoProperties.getKey());
    }

    public static String getKey() {
        return AesUtils.getKey();
    }

    /**
     * 敏感信息脱敏
     *
     * @param str 字符串
     * @return 敏感信息脱敏后的字符串
     */
    public static String desensitize(String str) {
        if (str == null) {
            return "";
        }
        return str.charAt(0) + "****" + str.charAt(str.length() - 1);
    }

}
