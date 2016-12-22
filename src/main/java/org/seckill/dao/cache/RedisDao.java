package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.bean.Seckill;
import org.seckill.dto.Exposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 * Created by ludi on 16/10/16.
 */
public class RedisDao {

    private Logger logger = LoggerFactory.getLogger(RedisDao.class);
    private JedisPool jedisPool;
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port) {
        this.jedisPool = new JedisPool(ip, port);
    }

    public Seckill getSeckill(long seckillId) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "seckill:" + seckillId;
            //Redis并没有实现内部序列化
            //get -> byte[] -> 反序列化 -> Object.
            //采用自定义序列化
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) { //
                //空对象
                Seckill seckill = schema.newMessage();
                //上面的空对象被反序列化
                ProtobufIOUtil.mergeFrom(bytes,seckill,schema);
                return seckill;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally {
            jedis.close();
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        Jedis jedis = null;
        try {// Ojbect -> 序列化 -> byte[]
            jedis = jedisPool.getResource();
            String key = "seckill:" + seckill.getSeckillId();
            byte[] bytes = ProtobufIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            //超时缓存
            int timeout = 60 * 60 ;//1小时
            String result = jedis.setex(key.getBytes(), timeout, bytes);
            return result;//结果
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return null;
    }
}
