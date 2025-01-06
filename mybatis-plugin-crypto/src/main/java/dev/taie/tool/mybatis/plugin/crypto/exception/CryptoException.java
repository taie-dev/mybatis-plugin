package dev.taie.tool.mybatis.plugin.crypto.exception;

/**
 * @date 2025/1/6
 */
public class CryptoException extends RuntimeException {
    private static final long serialVersionUID = 7300124372573776799L;

    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
