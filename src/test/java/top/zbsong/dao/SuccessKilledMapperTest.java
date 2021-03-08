package top.zbsong.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zbsong.pojo.SuccessKilled;

import static org.junit.Assert.*;

/**
 * Create By songzb on 2021/3/8
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-dao.xml")
public class SuccessKilledMapperTest {

    @Autowired
    SuccessKilledMapper successKilledMapper;
    @Test
    public void insertSuccessKilled() {
        int res = successKilledMapper.insertSuccessKilled(1000,1234567890);
        System.out.println(res);
    }

    @Test
    public void queryByIdWithSeckill() {
        SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(1000, 1234567890);
        System.out.println(successKilled.toString());
    }
}