package top.zbsong.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zbsong.pojo.Seckill;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Create By songzb on 2021/3/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-dao.xml")
public class SeckillMapperTest {
    @Autowired
    SeckillMapper seckillMapper;

    @Test
    public void reduceNumber() {
        int res = seckillMapper.reduceNumber(1001, new Date());
        System.out.println(res);
    }

    @Test
    public void queryById() {
        Seckill seckill = seckillMapper.queryById(1000);
        System.out.println(seckill.toString());
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillMapper.queryAll(0, 3);
        for(Seckill seckill : seckills) {
            System.out.println(seckill.toString());
        }
    }
}