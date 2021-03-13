package top.zbsong.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import top.zbsong.dao.SeckillMapper;
import top.zbsong.dao.SuccessKilledMapper;
import top.zbsong.dto.Exposer;
import top.zbsong.dto.SeckillExecution;
import top.zbsong.enums.SeckillStatEnum;
import top.zbsong.exception.RepeatKillException;
import top.zbsong.exception.SeckillCloseException;
import top.zbsong.exception.SeckillException;
import top.zbsong.pojo.Seckill;
import top.zbsong.pojo.SuccessKilled;
import top.zbsong.service.SeckillService;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;

/**
 * Create By songzb on 2021/3/13
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    /**
     * 日志对象
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 加入一个混淆字符串(秒杀接口)的salt，为了我避免用户猜出我们的md5值，值任意给，越复杂越好
     */
    private String salt = "129#21nshz.*&@.asda45";

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillMapper.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillMapper.queryById(seckillId);
        // 未查询到该商品的记录
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        // 秒杀未开启
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if (nowTime.getTime() < startTime.getTime() || endTime.getTime() < nowTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        //秒杀开启，返回秒杀商品的id、用给接口加密的md5
        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + salt;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
     * 使用注解控制事务方法的优点：
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作、只读操作不要事务控制
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            // 秒杀数据被重写了
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑:减库存+增加购买明细
        Date nowTime = new Date();
        try {
            // 减库存
            int reduceNumber = seckillMapper.reduceNumber(seckillId, nowTime);
            if (reduceNumber <= 0) {
                // 没有更新库存，说明秒杀结束
                throw new SeckillCloseException("seckill is closed");
            } else {
                // 秒杀成功，增加明细
                int insertNumber = successKilledMapper.insertSuccessKilled(seckillId, userPhone);
                if (insertNumber <= 0) {
                    // 若该明细已经插入，则会插入失败，表示用户重复秒杀
                    throw new RepeatKillException("seckill repeated");
                } else {
                    // 秒杀成功，返回成功秒杀的信息
                    SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error :" + e.getMessage());
        }
    }
}
