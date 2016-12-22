package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.bean.SuccessKilled;

/**
 * Created by ludi on 16/9/28.
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细,可过滤重复
     * @param seckillId
     * @param userPhone
     * @return 返回值代表插入的行数  返回>0 成功  =0 主键冲突,插入失败
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据Id查询SuccessKilled并携带秒杀对象实体
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone") long userPhone);

}
