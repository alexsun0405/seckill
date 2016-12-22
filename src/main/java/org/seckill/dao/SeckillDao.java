package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.bean.Seckill;

import java.util.List;
import java.util.Map;

/**
 * Created by ludi on 16/9/28.
 */
public interface SeckillDao {


    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 返回值代表的是影响的行数  如果return>0 成功
     */
    int reduceNumber(@Param("seckillId") long seckillId,@Param("killTime") long killTime);

    /**
     * 通过ID查询秒杀库存对象
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offseet
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offseet,@Param("limit") int limit);

    /**
     * 使用存储过程执行秒杀
     * @param params
     */
    void killByProcedure(Map<String, Object> params);

}
