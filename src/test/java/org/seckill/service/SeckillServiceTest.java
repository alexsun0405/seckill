package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.bean.Seckill;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by ludi on 16/10/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml",
                       "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(SeckillServiceTest.class);

    @Resource
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list {}", list);
    }

    @Test
    public void getById() throws Exception {
        Seckill seckill = seckillService.getById(1000);
        logger.info("seckill {}", seckill);
    }

    @Test
    public void exprotSeckillUrl() throws Exception {
        long id = 1000;
        Exposer exposer = seckillService.exprotSeckillUrl(id);
        logger.info("exposer={}", exposer);
    }

    @Test
    public void executeSeckill() throws Exception {
        try {
            long id = 1000;
            long phone = 13111111111L;
            String md5 = "271c51ced6a2de8258f90710025d2144";
            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
            logger.info("seckillExecution:{}", seckillExecution);
        } catch (RepeatKillException e) {
            logger.error(e.getMessage(), e);
        } catch (SeckillCloseException e) {
            logger.error(e.getMessage(), e);
        } catch (SeckillException e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 完整逻辑测试.
     */
    @Test
    public void seckillLogic() {
        long id = 1000;
        Exposer exposer = seckillService.exprotSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);
            String md5 = exposer.getMd5();
            long phone = 13222222222L;
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExecution:{}", seckillExecution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage(), e);
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage(), e);
            } catch (SeckillException e) {
                logger.error(e.getMessage(), e);
            }
        }else {
            //秒杀未开始
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void executeByProducer() {
        long id = 1000;
        Exposer exposer = seckillService.exprotSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer={}:", exposer);
            String md5 = exposer.getMd5();
            long phone = 13222222224L;
            SeckillExecution execution = seckillService.executeSeckillByProducer(id, phone, md5);
            logger.info("execution:{}" + execution);
        }
    }
}