package top.zbsong.service;

import top.zbsong.dto.Exposer;
import top.zbsong.dto.SeckillExecution;
import top.zbsong.exception.RepeatKillException;
import top.zbsong.exception.SeckillCloseException;
import top.zbsong.exception.SeckillException;
import top.zbsong.pojo.Seckill;

import java.util.List;

/**
 * Create By songzb on 2021/3/9
 */
public interface SeckillService {
    /**
     * 查询全部的秒杀记录
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);


    /**
     * 在秒杀开启时输出秒杀接口的地址，否则输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);


    /**
     * 执行秒杀操作，有可能失败，有可能成功，所以要抛出我们允许的异常
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException;
}

