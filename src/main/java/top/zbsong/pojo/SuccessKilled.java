package top.zbsong.pojo;


import java.util.Date;

/**
 * Create By songzb on 2021/3/6
 */
public class SuccessKilled {
    // 秒杀商品ID
    private long seckillId;
    // 用户手机号
    private long userPhone;
    // 状态标识：-1:无效 0:成功 1:已付款 2:已发货
    private boolean state;
    // 创建时间
    private Date createTime;

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
