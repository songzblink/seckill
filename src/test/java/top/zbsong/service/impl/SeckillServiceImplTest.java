package top.zbsong.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zbsong.dao.SeckillMapper;
import top.zbsong.dao.SuccessKilledMapper;
import top.zbsong.dto.Exposer;
import top.zbsong.dto.SeckillExecution;
import top.zbsong.exception.RepeatKillException;
import top.zbsong.exception.SeckillCloseException;
import top.zbsong.exception.SeckillException;
import top.zbsong.pojo.Seckill;
import top.zbsong.service.SeckillService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Create By songzb on 2021/3/13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-service.xml", "classpath:spring-dao.xml"})
public class SeckillServiceImplTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        System.out.println(seckillList);
    }

    @Test
    public void getById() {
        long seckillId = 1000;
        Seckill seckill = seckillService.getById(seckillId);
        System.out.println(seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long seckillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        System.out.println(exposer);
    }

    @Test
    public void executeSeckill() throws Exception {
        long seckillId = 1000;
        long userPhone = 12345678912L;
        String md5 = "2d8ae7568827183a29baadc820184da8";
        SeckillExecution seckillExecution = null;
        try {
            seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
            System.out.println(seckillExecution);
        } catch (RepeatKillException e) {
            e.printStackTrace();
        } catch (SeckillCloseException e1) {
            e1.printStackTrace();
        }
    }

    @Test
    public void testSeckillLogic() throws Exception {
        long seckillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            System.out.println(exposer);
            long userPhone = 11111111111L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
                System.out.println(seckillExecution);
            } catch (SeckillException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(exposer);
        }
    }
}