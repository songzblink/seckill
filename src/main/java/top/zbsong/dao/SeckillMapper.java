package top.zbsong.dao;

import top.zbsong.pojo.Seckill;

import java.util.Date;
import java.util.List;

/**
 * Create By songzb on 2021/3/6
 */
public interface SeckillMapper {
    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 更新库存的记录行数
     */
    int reduceNumber(long seckillId, Date killTime);

    /**
     * 根据id查询秒杀的商品信息
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param off 从第off个开始
     * @param limit 一共limit个
     * @return
     */
    List<Seckill> queryAll(int off, int limit);
}
