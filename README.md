秒杀系统实现记录

参考1：https://github.com/codingXiaxw/seckill

参考2：https://github.com/KolinHuang/seckill

## 1. 业务流程描述
**用户秒杀商品：**
 1. 减库存；
 2. 添加购买记录（谁购买，购买时间，商品交付信息）。

**系统添加事务的原因：**
1. 防止超卖：记录了购买明细，库存却没有成功减少；
2. 防止少卖：减少了库存，购买明细却没有成功记录。

## 2. 环境搭建
### 2.1 添加依赖
单元测试、日志、数据库相关依赖、servlet web相关、mybatis、Spring核心依赖、Spring-dao、spring-web依赖
```java
    <dependencies>
        <!--单元测试-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13</version>
        </dependency>

        <!--日志-->
        <!--
        日志 java日志有:slf4j,log4j,logback,common-logging
        slf4j:是规范/接口
        日志实现:log4j,logback,common-logging
        使用:slf4j+logback
        -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.12</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.1.1</version>
        </dependency>
        <!--实现slf4j接口并整合-->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.1.1</version>
        </dependency>

        <!--数据库连接池-->
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.5.2</version>
        </dependency>
        <!--数据库驱动-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.20</version>
        </dependency>


        <!--====servlet web相关依赖====-->
        <!--JSTL和EL表达式-->
        <dependency>
            <groupId>taglibs</groupId>
            <artifactId>standard</artifactId>
            <version>1.1.2</version>
        </dependency>
        <!--servlet-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>3.0-alpha-1</version>
        </dependency>
        <!--jsp-->
        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>jsp-api</artifactId>
            <version>2.2</version>
        </dependency>
        <!--jstl-->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
        <!--jason解析器-->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.11.2</version>
        </dependency>
        <!--====SSM框架====-->
        <!--mybatis-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.5</version>
        </dependency>
        <!--mybatis整合spring相关-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.1</version>
        </dependency>
        <!--spring 依赖-->
        <!--spring核心依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <!--spring-dao依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>

        <!--spring web依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
        <!--spring test依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>5.0.5.RELEASE</version>
        </dependency>
    </dependencies>
```

## 3. Dao层
### 3.1 创建数据库和表
```sql
CREATE DATABASE seckill;

USE seckill;


CREATE TABLE `seckill`(
	`seckill_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
	`name` VARCHAR(120) NOT NULL COMMENT '商品名称',
	`number` INT NOT NULL COMMENT '库存数量',
	`start_time` TIMESTAMP NOT NULL COMMENT '秒杀开始时间',
	`end_time` TIMESTAMP NOT NULL COMMENT '秒杀结束时间',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (seckill_id),
	KEY idx_start_time(start_time),
	KEY idx_end_time(end_time),
	KEY idx_create_time(create_time)
)ENGINE=INNODB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

INSERT INTO seckill(NAME,number,start_time,end_time)
VALUES
	('1000元秒杀iphone12', 100, '2020-10-30 00:00:00','2020-10-31 00:00:00'),
	('800元秒杀ipad pro', 200, '2020-10-30 00:00:00','2020-10-31 00:00:00'),
	('6600元秒杀iMac', 100, '2020-10-30 00:00:00','2020-10-31 00:00:00'),
	('7000元秒杀macbook pro', 100, '2020-10-30 00:00:00','2020-10-31 00:00:00');
	

CREATE TABLE success_killed(
	`seckill_id` BIGINT NOT NULL COMMENT '秒杀商品ID',
	`user_phone` BIGINT NOT NULL COMMENT '用户手机号',
	`state` TINYINT NOT NULL DEFAULT -1 COMMENT '状态标识：-1:无效 0:成功 1:已付款 2:已发货',
	`create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY(seckill_id, user_phone),/*联合主键？*/
	KEY idx_create_time(create_time)
)ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';
```

### 3.2 创建实体类
在 `top.zbsong.pojo` 包下创建实体类 Seckill.java 和 SuccessKilled.java 。
```java
public class Seckill {
    // 商品库存ID
    private long seckillId;
    // 商品名称
    private String name;
    // 库存数量
    private int number;
    // 秒杀开始时间
    private Date startTime;
    // 秒杀结束时间
    private Date endTime;
    // 创建时间
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
```

```java
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
```
### 3.3 创建dao接口
在 `top.zbsong.dao` 包下，创建接口 seckillMapper.java 和 SuccessKilledMapper.java。
```java
public interface SeckillMapper {

    /**
     * 减库存
     * @param seckillId
     * @param killTime
     * @return 更新库存的记录行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);

    /**
     * 根据id查询秒杀的商品信息
     * @param seckillId
     * @return
     */
    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset 从第off个开始
     * @param limit 一共limit个
     * @return
     */
    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);
}
```
```java
public interface SuccessKilledMapper {
    /**
     * 插入购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据秒杀商品的id查询明细SuccessKilled对象(该对象携带了Seckill秒杀产品对象)
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
```

### 3.4 动态代理实现dao接口
mybatis全局配置文件 mybatis-config.xml。

需要在浏览器中输入 `http://mybatis.github.io/mybatis-3/zh/index.html` 打开MyBatis的官网文档，点击左边的"入门"栏框，找到mybatis全局配置文件，在这里有xml的一个规范，也就是它的一个xml约束，填充到配置文件头部。
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
<!-- 上面为约束头-->
<configuration>
    <settings>
        <!--
        使用jdbc的getGeneratekeys获取自增主键值
        即当插入的类对象不包含主键时，自动获取自增主键值并赋值给要插入的类对象
        -->
        <setting name="useGeneratedKeys" value="true"/>
        <!--开启驼峰命名转换Table:create_time到 Entity(createTime)-->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
    </settings>
<!--在整合Spring和Mybatis之后，下面的配置可以放到Spring配置文件中-->
    <typeAliases>
        <!--<typeAlias type="com.songzb.domain.Account" alias="account"/>-->
        <!--这样配置的话，该包下面的类都会有别名，别名为首字母小写的类名-->
        <package name="top.zbsong.pojo"/>-->
    </typeAliases>

   <mappers>
        <package name="top.zbsong.dao"/>
   </mappers>
</configuration>
```
注意，映射配置的配置文件必须与接口同名。

XxxMapper.xml 文件的存放有两种方法：

- XxxMapper.xml 与 XxxMaper.java 接口文件放在同一个包下面：
那么需要在 pom.xml 文件中加入处理静态资源的配置
```xml
    <build>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.xml</include>
                </includes>
            </resource>
        </resources>
    </build>
```
然后在 mybatis-config.xml 中配置：
```xml
    <mappers>
        <package name="top.zbsong.dao"/>
    </mappers>
``` 
- XxxMapper.xml 直接在 `resoutces` 目录下存放：
 然后在 mybatis-config.xml 中配置：
 ```xml
    <mappers>
        <mapper resource="mapper/SeckillMapper.xml"/>
        <mapper resource="mapper/SuccessKilledMapper.xml"/>
    </mappers> 
```
或者在在之后整合Spring和Mybatis之后在 spring-dao.xml 中配置：
```xml
    <!--３.配置SqlSessionFactory对象-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!--扫描sql配置文件:mapper需要的xml文件-->
        <property name="mapperLocations" value="classpath:mapper/*.xml"/>
    </bean>
```
编写对应Dao接口的xml映射文件，同样需要xml约束，在官方文档中点击左侧”XML配置“，在它的一些实例中找到。
`SeckillMapper.xml`：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="top.zbsong.dao.SeckillMapper">
    <!--这里需要提供命名空间，与下面语句的id一起组成查询的标识，必须是完整的路径-->

    <update id="reduceNumber">
        UPDATE seckill
        SET number = number-1
        WHERE seckill_id=#{seckillId}
        AND start_time <![CDATA[ <= ]]> #{killTime}
        AND end_time >= #{killTime}
        AND number > 0
    </update>

    <select id="queryById" resultType="seckill" parameterType="long">
        SELECT * FROM seckill
        WHERE seckill_id=#{seckillId};
    </select>

    <select id="queryAll" resultType="seckill">
        SELECT * FROM seckill
        ORDER BY create_time DESC
        limit #{offset},#{limit};
    </select>
</mapper>
```

`SuccessKilledMapper.xml`：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="top.zbsong.dao.SuccessKilledMapper">
    <insert id="insertSuccessKilled">
        <!--当出现主键冲突时(即重复秒杀时)，会报错;不想让程序报错，加入ignore-->
        INSERT ignore INTO success_killed(seckill_id,user_phone,state)
        VALUES (#{seckillId},#{userPhone},0)
    </insert>

    <select id="queryByIdWithSeckill" resultType="successKilled">
        SELECT
            sk.seckill_id,
            sk.user_phone,
            sk.create_time,
            sk.state,
            s.seckill_id "seckil.seckill_id",
            s.name "seckill.name",
            s.number "seckill",
            s.start_time "seckill.start_time",
            s.end_time "seckill.end_time",
            s.create_time "seckill.create_time"
        FROM success_killed sk
        INNER JOIN seckill s ON sk.seckill_id=s.seckill_id
        WHERE sk.seckill_id=#{seckillId}
        AND sk.user_phone=#{userPhone}
    </select>
</mapper>
```

### 3.5 整合Spring和Mybatis
编写 `spring-dao.xml`，让Spring管理数据库连接池和dao接口的动态注入：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <!--配置整合mybatis过程-->
    <!--1.关联数据库配置文件-->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--2.配置数据源信息-->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!--配置连接池属性-->
        <property name="driverClass" value="${jdbc.driver}"/>
        <!-- 基本属性 url、user、password -->
        <property name="jdbcUrl" value="${jdbc.url}"/>
        <property name="user" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>

        <!--c3p0私有属性-->
        <property name="maxPoolSize" value="30"/>
        <property name="minPoolSize" value="10"/>
        <!--关闭连接后不自动commit-->
        <property name="autoCommitOnClose" value="false"/>

        <!--获取连接超时时间-->
        <property name="checkoutTimeout" value="1000"/>
        <!--当获取连接失败重试次数-->
        <property name="acquireRetryAttempts" value="2"/>
    </bean>

    <!--３.配置SqlSessionFactory对象-->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!--往下才是mybatis和spring真正整合的配置-->
        <!--注入数据库连接池-->
        <property name="dataSource" ref="dataSource"/>
        <!--配置mybatis全局配置文件:mybatis-config.xml-->
        <property name="configLocation" value="classpath:mybatis-config.xml"/>
        <!--扫描entity包,使用别名,多个用;隔开-->
        <property name="typeAliasesPackage" value="top.zbsong.pojo"/>
        <!--扫描sql配置文件:mapper需要的xml文件-->
        <property name="mapperLocations" value="classpath:mapper/*.xml"/>
    </bean>

    <!--４:配置扫描Dao接口包,动态实现DAO接口,注入到spring容器-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--注入SqlSessionFactory-->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!--给出需要扫描的Dao接口-->
        <property name="basePackage" value="top.zbsong.dao"/>
    </bean>
</beans>
```

### 3.6 dao层测试
测试 SeckillMapper.java 接口方法
```java
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
```

测试 SuccessKilledMapper.java 接口：

```java
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
```

## 4. Service层
### 4.1 创建业务接口
编写业务层接口，有两个重要的方法：1、暴露秒杀接口的地址。2、处理秒杀
```java
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
```

### 4.2 dto封装类
建立一个包dto，用于封装业务层给web传输的数据，其中包括上面两个重要方法的返回值封装：Exposer 和 SeckillExecution：
```java
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
```
```java
public class SeckillExecution {

    private long seckillId;

    /**
     * 秒杀执行结果的状态
     */
    private int state;

    /**
     * 状态的明文标识
     */
    private String stateInfo;

    /**
     * 当秒杀成功时，需要传递秒杀成功的对象回去
     */
    private SuccessKilled successKilled;

    /**
     * 秒杀成功返回秒杀明细信息
     */
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getInfo();
        this.successKilled = successKilled;
    }

    /**
     * 秒杀失败
     */
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getInfo();
    }

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successKilled=" + successKilled +
                '}';
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessKilled getSuccessKilled() {
        return successKilled;
    }

    public void setSuccessKilled(SuccessKilled successKilled) {
        this.successKilled = successKilled;
    }
}
```

### 4.3 异常处理类
创建一个exception包，用于处理异常，主要有两个异常：1、重复秒杀异常；2、秒杀结束异常。
```java
/**
 * 秒杀相关的所有业务异常
 * Create By songzb on 2021/3/9
 */
public class SeckillException extends RuntimeException {
    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
```java
/**
 * 重复秒杀异常，是一个运行期异常，不需要我们手动try catch
 * Mysql只支持运行期异常的回滚操作
 * Create By songzb on 2021/3/9
 */
public class RepeatKillException extends SeckillException {
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
```
```java
/**
 * 秒杀关闭异常，当秒杀结束时用户还要进行秒杀就会出现这个异常
 * Create By songzb on 2021/3/9
 */
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

### 4.4 业务层接口的实现
```java
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
```

分析上述代码，我们返回的 state 和 stateInfo 参数信息应该是输出给前端的，但是我们不想在我们的 return 代码中硬编码这两个参数，所以我们应该考虑用枚举的方式将这些常量封装起来，在 `top.zbsong` 包下新建一个枚举包 enums，创建一个枚举类型 SeckillStatEnum.java。
```java
public enum SeckillStatEnum {
    SUCCESS(1, "秒杀成功"),
    END(0, "秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATE_REWRITE(-3, "数据篡改");
    private int state;
    private String info;

    SeckillStatEnum(int state, String info) {
        this.state = state;
        this.info = info;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
```

### 4.5 将Service层交给Spring管理
创建 spring-service.xml，配置扫描包，注入service的bean
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--扫描service包下所有使用注解的类型-->
    <context:component-scan base-package="top.zbsong.service"/>
    <!--配置事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--注入数据库连接池-->
        <property name="dataSource" ref="dataSource"/>
    </bean>

</beans>
```

### 4.6 使用Spring的声明式事务配置
声明式事务的使用方式：
1. 早期使用的方式：ProxyFactoryBean+XML。
2. tx:advice+aop 命名空间，这种配置的好处是一次配置永久生效。
3. 注解 @Transactional 的方式。
在实际开发中，建议使用第三种方式进行事务控制。继续在 spring-service.xml 中添加一下配置：
```xml
  <!--配置基于注解的声明式事务，默认使用注解来管理事务行为-->
  <tx:annotation-driven transaction-manager="transactionManager"/>
```
然后在Service实现类方法中，在需要进行事务声明的方法上加上事务的注解：`@Transactional`。

使用注解控制事务方法的优点：

- 开发团队达成一致约定，明确标注事务方法的编程风格；
- 保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部；
- 不是所有的方法都需要事务，如果只有一条修改操作、只读操作不需要事务控制。

### 4.7 Service逻辑的集成测试
```java
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
```

## 5. web层

### 5.1 整合Spring MVC框架
创建 spring-web.xml 配置文件，并开启注解模式、配置静态资源、扫描包、视图解析器
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc.xsd">
    <!--配置spring mvc-->
    <!--1.开启springMVC注解模式
        a.自动注册DefaultAnnotationHandlerMapping，AnnotationMethodHandlerAdapter
        b.默认提供一系列的功能：数据绑定，数字和日期的format@NumberFormat，@DateTimeFormat
        c.xml，json的默认读写支持
    -->
    <mvc:annotation-driven/>

    <!--2.静态资源默认servlet配置
        a.加入对静态资源处理：js，gif，png
        b.允许使用"/"做整体映射
    -->
    <mvc:default-servlet-handler/>

    <!--3.配置JSP显示viewResolver-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp"/>
        <property name="suffix" value=".jsp"/>
    </bean>

    <!--4.扫描web相关的bean，即Controller-->
    <context:component-scan base-package="top.zbsong.web"/>
</beans>
```

### 5.2 导入静态资源

将 `src/main/web` 目录下的文件拷贝到自己的 `src/main/web` 目录下

### 5.3 结果封装类
在 `top.zbsong.dto` 包下创建 SeckillResult.java 类，用于封装md5地址和秒杀结果，给前端传值。
```java
/**
 * 将所有的ajax请求返回类型，全部封装成json数据
 */
public class SeckillResult<T> {
    private boolean success;
    private T data;
    private String error;

    /**
     * 成功
     * @param success
     * @param data
     */
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    /**
     * 失败
     * @param success
     * @param error
     */
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
```

### 5.4 编写Controller
```java
@Controller
// url:模块/资源/{}/细分
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    SeckillService seckillService;

    // 访问商品的列表页
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        // list.jsp + model = ModelAndView
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
    }

    // 访问商品的详情页
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    // 返回一个json数据，数据中封装了我们商品的秒杀地址
    @RequestMapping(value = "/{seckillId}/exposer", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            e.printStackTrace();
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    // 封装用户是否秒杀成功的信息
    @RequestMapping(value = "/{seckillId}/{md5}/execution", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "userPhone", required = false) Long phone) {
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e1) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(false, execution);
        } catch (SeckillCloseException e2) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(false, execution);
        } catch (SeckillException e3) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(false, execution);
        }
    }

    // 获取系统时间
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date nowTime = new Date();
        return new SeckillResult<Long>(true, nowTime.getTime());
    }
}
```

- `@ResponseBody` 注解表示该方法的返回结果直接写入 HTTP 响应正文中，一般在异步获取数据时使用；
- 在使用 `@RequestMapping` 后，返回值通常解析为跳转路径，加上 `@Responsebody` 后返回结果不会被解析为跳转路径，而是直接写入HTTP 响应正文中。例如，异步获取json数据，加上 `@Responsebody` 注解后，就会直接返回json数据。
- `@RequestBody` 注解则是将 HTTP 请求正文插入方法中，使用适合的 HttpMessageConverter 将请求体写入某个对象。

## 6. 添加Redis缓存
### 6.1 整合Dao层
在 dao 包下创建一个包 cache，包下创建 RedisMapper.java 文件：
```java
public class RedisMapper {
    private final JedisPool jedisPool;

    public RedisMapper(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    /**
     * 序列化工具类
     */
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId) {
        return getSeckill(seckillId, null);
    }

    /**
     * 从redis获取信息
     *
     * @param seckillId id
     * @return 如果不存在，则返回null
     */
    public Seckill getSeckill(long seckillId, Jedis jedis) {
        boolean hasJedis = jedis != null;
        //redis操作逻辑
        try {
            if (!hasJedis) {
                jedis = jedisPool.getResource();
            }
            try {
                String key = getSeckillRedisKey(seckillId);
                //并没有实现哪部序列化操作
                //采用自定义序列化
                //protostuff: pojo.
                byte[] bytes = jedis.get(key.getBytes());
                //缓存重获取到
                if (bytes != null) {
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    //seckill被反序列化

                    return seckill;
                }
            } finally {
                if (!hasJedis) {
                    jedis.close();
                }
            }
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * 从缓存获取，如果没有，则从数据库获取
     * 会用到分布式锁
     *
     * @param seckillId     id
     * @param getDataFromDb 从数据库获取的方法
     * @return 返回商品信息
     */
    public Seckill getOrPutSeckill(long seckillId, Function<Long, Seckill> getDataFromDb) {

        String lockKey = "seckill:locks:getSeckill:" + seckillId;
        String lockRequestId = UUID.randomUUID().toString();
        Jedis jedis = jedisPool.getResource();

        try {
            // 循环直到获取到数据
            while (true) {
                Seckill seckill = getSeckill(seckillId, jedis);
                if (seckill != null) {
                    return seckill;
                }
                // 尝试获取锁。
                // 锁过期时间是防止程序突然崩溃来不及解锁，而造成其他线程不能获取锁的问题。过期时间是业务容忍最长时间。
                boolean getLock = JedisUtils.tryGetDistributedLock(jedis, lockKey, lockRequestId, 1000);
                if (getLock) {
                    // 获取到锁，从数据库拿数据, 然后存redis
                    seckill = getDataFromDb.apply(seckillId);
                    putSeckill(seckill, jedis);
                    return seckill;
                }

                // 获取不到锁，睡一下，等会再出发。sleep的时间需要斟酌，主要看业务处理速度
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
        } catch (Exception ignored) {
        } finally {
            // 无论如何，最后要去解锁
            JedisUtils.releaseDistributedLock(jedis, lockKey, lockRequestId);
            jedis.close();
        }
        return null;
    }

    /**
     * 根据id获取redis的key
     *
     * @param seckillId 商品id
     * @return redis的key
     */
    private String getSeckillRedisKey(long seckillId) {
        return "seckill:" + seckillId;
    }

    public String putSeckill(Seckill seckill) {
        return putSeckill(seckill, null);
    }

    public String putSeckill(Seckill seckill, Jedis jedis) {
        boolean hasJedis = jedis != null;
        try {
            if (!hasJedis) {
                jedis = jedisPool.getResource();
            }
            try {
                String key = getSeckillRedisKey(seckill.getSeckillId());
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                //超时缓存，1小时
                int timeout = 60 * 60;
                String result = jedis.setex(key.getBytes(), timeout, bytes);

                return result;
            } finally {
                if (!hasJedis) {
                    jedis.close();
                }
            }
        } catch (Exception e) {

        }

        return null;
    }
}
```
需要用到分布式锁，所以创建一个工具类 `JedisUtil`，利用 `set lock:xx true ex 5 nx` 来实现原子操作：
```java
public class JedisUtils {
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 尝试获取分布式锁
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间, 单位毫秒
     * @return 是否获取成功
     */
    public static boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        return LOCK_SUCCESS.equals(result);
    }

    /**
     * 释放分布式锁
     *
     * @param jedis     Redis客户端
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public static boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        return result.equals(RELEASE_SUCCESS);

    }
}
```

在 `sping-dao.xml` 中配置 bean：
```xml
<!--redis-->
<bean id="redisMapper" class="top.zbsong.dao.cache.RedisMapper">
    <constructor-arg index="0" value="localhost"/>
    <constructor-arg index="1" value="6379"/>
</bean>
```

### 6.2 整合Service层
为 service 注入 `redisMapper`：
```java
/**
 * 注入redisMapper
 */
@Autowired
private RedisMapper redisMapper;

public void setRedisMapper(RedisMapper redisMapper) {
    this.redisMapper = redisMapper;
}
```
修改查询逻辑，优先查询 Redis：
```java
/**
 * 修改查询逻辑，优先查询Redis
 *
 * @return
 */
@Override
public Seckill getById(long seckillId) {
    // return seckillMapper.queryById(seckillId);
    return redisMapper.getOrPutSeckill(seckillId, id -> seckillMapper.queryById(id));
}
```

遇到的问题：
```
Lookup method resolution failed; nested exception is java.lang.IllegalStateE
Resolution of declared constructors on bean Class [top.zbsong.dao.cache.RedisMapper] from ClassLoader [ParallelWebappClassLoader
```
出现的原因：更新了 `pom.xml` 添加 Redis 相关依赖后没有在 lib 文件下添加相应依赖。

解决办法：File -> Project Structure -> Artifacts -> WEB-INF -> lib 添加响应的依赖 jar 包即可。