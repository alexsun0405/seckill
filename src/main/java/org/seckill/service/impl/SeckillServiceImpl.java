package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.bean.Seckill;
import org.seckill.bean.SuccessKilled;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by ludi on 16/10/15.
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(SeckillServiceImpl.class);

    //md5盐值字符串,用于md5混淆
    private final String salt = "sadffweljsdfa7sdf89s8a0dfa990D&^*Y&Y&^^";

    @Resource
    private SeckillDao seckillDao;
    @Resource
    private SuccessKilledDao successKilledDao;
    @Resource
    private RedisDao redisDao;

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exprotSeckillUrl(long seckillId) {

        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                redisDao.putSeckill(seckill);
            }
        }

        //通过Redis,优化这一步
//        Seckill seckill = seckillDao.queryById(seckillId);



        if (seckill == null) {
            return new Exposer(false, seckillId);
        }

        long startTime = seckill.getStartTime().getTime();
        long endTime = seckill.getEndTime().getTime();
        long now = System.currentTimeMillis();
        if (now < startTime || now > endTime) {//秒杀未开启/秒杀已结束
            return new Exposer(false, seckillId, now, startTime, endTime);
        }

        //转化特定字符串的过程,不可逆.
        String md5 = getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    /**
     * 使用注解控制事务方法的优点:
     * 1.开发团队达成一致约定,明确标注事务方法的编程风格.
     * 2.保证事务方法的执行时间尽可能的短,不要穿插其他的网络操作,RPC/HTTP请求或者剥离到事务方法外.
     * 3.不是所有的方法都需要事务.
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑
        try {
            //2.记录秒杀操作
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //1.减库存
                int updataCount = seckillDao.reduceNumber(seckillId, System.currentTimeMillis());
                if (updataCount <= 0) {
                    //秒杀结束了,此时抛出了运行时异常,spring会进行rollback操作
                    throw new SeckillCloseException("seckill is close");
                } else {
                    //秒杀成功,return正常,spring会进行commit操作
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }

        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常,转化为运行期异常,可以回滚.
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }

    }

    /**
     * 调用存储过程执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     */
    public SeckillExecution executeSeckillByProducer(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        long time = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<String,Object>();
        params.put("seckillId", seckillId);
        params.put("phone", userPhone);
        params.put("killTime", time);
        params.put("result", null);
        try {
            //执行存储过程
            seckillDao.killByProcedure(params);
            Integer result = MapUtils.getInteger(params, "result", -2);
            if (result == 1) {
                SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
}
