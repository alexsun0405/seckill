package org.seckill.exception;

/**
 * 重复秒杀异常(运行期异常)
 * spring只回滚运行期异常
 * Created by ludi on 16/10/15.
 */
public class RepeatKillException extends SeckillException {

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
