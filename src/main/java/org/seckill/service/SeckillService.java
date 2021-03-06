package org.seckill.service;

import org.seckill.bean.Seckill;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * 业务接口:站在使用者的角度设计接口
 * 三个方面: 方法定义粒度,参数,返回类型.
 * Created by ludi on 16/10/15.
 */
public interface SeckillService {

    /**
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时,输出秒杀接口地址,
     * 否则输出系统时间和秒杀开始时间
     * @param seckillId
     */
    Exposer exprotSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException;

    /**
     * 执行秒杀操作 通过存储过程
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckillByProducer(long seckillId, long userPhone, String md5);

}
