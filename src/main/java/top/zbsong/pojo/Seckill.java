package top.zbsong.pojo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Create By songzb on 2021/3/6
 */
public class Seckill {
    @ApiModelProperty("商品库存ID")
    private long seckillId;
    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("库存数量")
    private int number;
    @ApiModelProperty("秒杀开始时间")
    private Date startTime;
    @ApiModelProperty("秒杀结束时间")
    private Date endTime;
    @ApiModelProperty("创建时间")
    private Date createTime;

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
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
