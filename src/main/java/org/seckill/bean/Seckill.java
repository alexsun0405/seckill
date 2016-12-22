package org.seckill.bean;

import java.util.Date;

/**
 * Created by ludi on 16/9/28.
 */
public class Seckill {


    private long seckillId;
    private String name;
    private int number;
    private long startTime;
    private long endTime;
    private long createTime;

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Date getStartTime() {
        return new Date(startTime);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return new Date(endTime);
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return new Date(createTime);
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Seckill{" +
                "seckillId=" + seckillId +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", createTime=" + createTime +
                '}';
    }
}
