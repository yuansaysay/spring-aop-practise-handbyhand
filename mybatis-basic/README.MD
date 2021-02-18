### **MyBatis 独立使用基础方式**
尽管我们接触更多的是MyBatis与Spring的整合使用，但是MyBatis有他独立使用的方法，了解其独立使用的方法套路对分析Spring整合MyBatis非常有帮助，因为Spring无非就是将这些功能进行封装以简化我们的开放里流程。

了解独立使用MyBatis方式，有利于理解spring集成MyBatis幕后行为。

### **MyBatis独立使用包括一下几步关键点**

#### 建立PO
用于对数据库中数据的映射，使程序员更关注于对Java类的使用而不是数据库的操作。
参照：model.TUser

#### 建立Mapper
数据库操作的映射文件，也就是我们常常说的DAO，用于映射数据库的操作，可以通过配置文件指定方法对应的SQL语句或者直接使用Java提供的注解方式进行SQL指定。
参照：dao.TUserMapper

##### 建立配置文件
配置文件主要用于配置程序中可变性高的设置，MyBatis中的配置文件主要封装在configuration中，MyBatis 的配置文件包含了会深深影响 MyBatis 行为的设置和属性信息。 
配置文档的顶层结构如下：
- configuration（配置根元素）
    * properties（属性）
    * settings（定义一些全局性的配置）
    * typeAliases（为一些类定义别名）
    * typeHandlers（类型处理器）
    * objectFactory（对象工厂，用于指定结果集对象的实例是如何创建的）
    * plugins（MyBatis的插件，插件可以修改MyBatis内部的运行规则）
    * environments（环境配置）
        * environment 环境变量
        * transactionManager 事务管理器
        * dataSource 数据源
    * environment（环境变量）
    * transactionManager（事务管理器）
    * dataSource（数据源）
    * databaseIdProvider（数据库厂商标识）
    * mappers（映射器，指定映射文件或者映射类）
    
上面的configuration结构对应xml的元素，
如下xml仅关注基础功能。
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!--导入db.properties文件中的所有key-value数据-->
    <properties resource="db.properties">

    </properties>
    <settings>
        <setting name="cacheEnabled" value="true"/>
        <setting name="useGeneratedKeys" value="true"/>
        <setting name="defaultExecutorType" value="REUSE"/>
        <!--设置日志输出的目标-->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>
    <typeAliases>
        <typeAlias type="model.TUser" alias="user"></typeAlias>
    </typeAliases>
    <!--环境配置，default为默认选择的环境-->
    <environments default="development">

        <environment id="development">
            <!--事务管理-->
            <transactionManager type="JDBC"/>
            <!--连接池-->
            <dataSource type="POOLED">
                <property name="driver" value="${spring.datasource.current.driver-class-name}"/>
                <property name="url" value="${spring.datasource.current.jdbc-url}"/>
                <property name="username" value="${spring.datasource.current.username}"/>
                <property name="password" value="${spring.datasource.current.password}"/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="mapper/TUserMapper.xml"></mapper>
    </mappers>
</configuration>
```    
#### 建立映射文件
对应于MyBatis全局配置中的mappers配置属性，主要用于建立对应数据库操作接口的SQL映射。MyBatis会将这里设定的SQL与对应的Java接口相关联，以保证在MyBatis中调用的时候会到数据库中执行相应的SQL来简开发。
参照：上面 configuration中的mappers元素指向的xml文件

### 建立测试类
至此我们已经完成了MyBatis的建立过程，接下来的工作就是对之前的所有工作进行测试，以便查看MyBatis为我们提供的的效果。
```java
import dao.TUserMapper;
import model.TUser;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import utils.MyBatisUtil;

/**
 *  独立使用 MyBatis 的使用方式，
 *
 *  了解独立使用 Mybatis，有助于理解spring集成 MyBatis 背后所做的事情。
 */
public class TestMapper {

    static SqlSessionFactory sqlSessionFactory = null;

    static {
        sqlSessionFactory = MyBatisUtil.getSqlSessionFactory();
    }

    /**
    * 这里我们简单测试一下新增
    */
    @Test
    public void testAdd() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 这里是编程式使用mybatis的特征,基于JDK动态代理完成sql操作。
        // 在spring 集成 mybatis 的源码中其实有这个特征代码，毕竟要做的事是同一个嘛。
        // 这里可以留意一下
        TUserMapper userMapper = sqlSession.getMapper(TUserMapper.class);
        try {
            TUser tUser = new TUser();
            tUser.setName("name");
            userMapper.insertSelective(tUser);
            // 一定要手动提交，不会插入数据
            sqlSession.commit();
        } finally {
            // 释放连接
            sqlSession.close();
        }
    }
}

```

### 进阶
我们了解了MyBatis的基础使用方式，可以假想Spring集成MyBatis的源码里肯定会有相同或相似代码完成此功能。
以这种先入为主的思路去学习Spring源码，还可以。

`实际上Spring集成MyBatis关键组件有:`

- **org.mybatis.spring.SqlSessionFactoryBean**

- **org.mybatis.spring.mapper.MapperFactoryBean**

- **org.mybatis.spring.mapper.MapperScannerConfigurer**