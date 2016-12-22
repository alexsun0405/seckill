-- 数据库初始化脚本
CREATE DATABASE seckill;

-- 使用数据库
use seckill;

-- 创建秒杀库存表
CREATE TABLE seckill(
  seckill_id BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
  name VARCHAR(120)NOT NULL COMMENT '商品名称',
  number INT NOT NULL COMMENT '库存数量',
  create_time BIGINT NOT NULL  COMMENT '创建时间',
  start_time BIGINT NOT NULL COMMENT '秒杀开启时间',
  end_time BIGINT NOT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY(seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)ENGINE = INNODB AUTO_INCREMENT = 1000 DEFAULT CHARSET = utf8 COMMENT = '秒杀库存表'

-- 初始化数据
  INSERT INTO seckill (NAME, NUMBER, start_time, end_time,create_time)
VALUES
('1000元秒杀Iphone6',100,1477929600000,1478016000000,1477843200000),
('500元秒杀Ipad2',200,1477929600000,1478016000000,1477843200000),
('300元秒杀小米4',300,1477929600000,1478016000000,1477843200000),
('200元秒杀红米note',400,1477929600000,1478016000000,1477843200000);

-- 秒杀成功明细表
CREATE TABLE success_killed(
  seckill_id BIGINT NOT NULL COMMENT '秒杀商品ID',
  user_phone BIGINT NOT NULL COMMENT '用户手机号',
  state TINYINT NOT NULL DEFAULT -1 COMMENT '状态标示: -1:无效 0:成功 1:已付款 2:已收货',
  create_time BIGINT NOT NULL COMMENT '创建时间'
  PRIMARY KEY (seckill_id,user_phone),
  KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT ='秒杀成功明细表'