package top.zbsong.dto;

/**
 * 封装秒杀的地址信息，暴露秒杀地址(接口)DTO
 * Create By songzb on 2021/3/9
 */
public class Exposer {
    /**
     * 是否开启秒杀
     */
    private boolean exposed;

    /**
     * 对秒杀地址加密措施
     */
    private String md5;

    /**
     * id为seckillId的商品的秒杀地址
     */
    private long seckillId;

    /**
     * 系统当前时间(毫秒)
     */
    private long now;

    /**
     * 秒杀的开启时间
     */
    private long start;

    /**
     * 秒杀的结束时间
     */
    private long end;

    /**
     * 开启秒杀成功，返回true，seckillId对应的md5，seckillId
     *
     * @param exposed
     * @param md5
     * @param seckillId
     */
    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    /**
     * 若秒杀暂未开启则返回false、seckillId和当前系统时间、秒杀开启时间、秒杀结束时间
     *
     * @param exposed
     * @param seckillId
     * @param now
     * @param start
     * @param end
     */
    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    /**
     * 未查询到则返回false和seckillId
     *
     * @param exposed
     * @param seckillId
     */
    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }

    @Override
    public String toString() {
        return "Exposer{" +
                "exposed=" + exposed +
                ", md5='" + md5 + '\'' +
                ", seckillId=" + seckillId +
                ", now=" + now +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed(boolean exposed) {
        this.exposed = exposed;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
