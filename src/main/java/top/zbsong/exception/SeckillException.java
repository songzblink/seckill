package top.zbsong.exception;

/**
 * 秒杀相关的所有业务异常
 * Create By songzb on 2021/3/9
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
