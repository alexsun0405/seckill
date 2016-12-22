-- 秒杀之行存储过程
DELIMITER $$ -- 这一行时声明 换行符由原来的 ; 变成 $$
-- 定义存储过程
-- row_count() 函数,返回上一条修改类型的sql的影响行数.
-- row_count() 0:未修改, >0 修改的行数  <0 sql错误/未执行sql
CREATE PROCEDURE execute_seckill
  (in v_seckill_id bigint,in v_phone BIGINT,
   in v_kill_time TIMESTAMP,out r_result INT)
BEGIN
  DECLARE insert_count int DEFAULT 0;
  START TRANSACTION ;
  INSERT IGNORE INTO success_killed
  (seckill_id, user_phone, create_time)
  VALUES (v_seckill_id, v_phone, 0);
  select row_count() into insert_count;
  IF (insert_count < 0) THEN
    ROLLBACK ;
    set r_result = -1;
  ELSEIF (insert_count < 0) THEN
    ROLLBACK ;
    set r_result = -2;
  ELSE
    update seckill
    set number = number-1
    where seckill_id = v_seckill_id
    and end_time > v_kill_time
    and start_time < v_kill_time
    and number > 0;

    select row_count() into insert_count;
    IF (insert_count = 0) THEN
      ROLLBACK ;
      set r_result=0;
    ELSEIF (insert_count < 0) THEN
      ROLLBACK ;
      set r_result=-2;
    ELSE
      COMMIT ;
      set r_result = 1;
    END IF;
  END IF;
END ;
$$
-- 存储过程定义结束

DELIMITER ;

set @r_result = -3;
CALL execute_seckill(1003, 13111111111, now(), @r_result);

-- 获取结果
