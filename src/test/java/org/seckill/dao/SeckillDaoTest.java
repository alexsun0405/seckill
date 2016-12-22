package org.seckill.dao;

import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by ludi on 16/9/28.
 */

public class SeckillDaoTest extends BaseTest {
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() throws Exception {
        System.out.println(seckillDao.reduceNumber(1000, 1477972800000l));
    }

    @Test
    public void queryById() throws Exception {

    }

    @Test
    public void queryAll() throws Exception {

    }
}