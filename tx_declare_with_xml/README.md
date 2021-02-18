完全基于XML的 spring 事务基础配置方式之一,只关注事务相关配置                


> 基于XML配置的Spring，似乎更能体现并透露出Spring的设计理念和实现方式。
> 本文就完全只关注基于XML的，声明式事务的配置。
> 在Spring事务的学习道路上，以最小化目标去实践，理解，可能是个和Spring这个庞然大物相处不错的方式。        

### AOP基础认知
基础但是重要的知识
`Spring的声明式事务是基于AOP的，所以应用配置里需要配置AOP相关组件。`
一个常规AOP的拦截操作，需要向容器中提供一个切点类型的Advisor（可用看做Advice+Pointcut）
Advice: 拦截器，用于定义AOP的切面逻辑
Pointcut: 用于定义切面匹配的方式

上述AOP概念应用到Spring事务配置，其实就是
Advisor:    可以通过 <aop:config> 标签来向容器中配置 Advisor
Advice: TransactionInterceptor.class，可用通过 <tx:advice> 标签将该Advice配置进容器
Pointcut:   常规的，定义一个表达式
                   
### 主要应用配置

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-3.0.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 加载jdbc.properties文件 -->
    <context:property-placeholder location="classpath:application.properties"/>

    <!-- 配置连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${spring.datasource.current.driver-class-name}"></property>
        <property name="jdbcUrl" value="${spring.datasource.current.jdbc-url}"></property>
        <property name="user" value="${spring.datasource.current.username}"></property>
        <property name="password" value="${spring.datasource.current.password}"></property>
    </bean>

    <!-- 配置事务核心管理器,封装了所有的事务操作,依赖于连接池 -->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <!-- 配置jdbc模板对象，这里直接使用Spring提供的模板，就不引入Mybatis了，比较这里主要关注的是 Spring 声明式事务的本质！！！ -->
    <bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">
        <property name="dataSource" ref="dataSource"></property>
    </bean>

    <bean id="userService" class="UserServiceImpl">
        <property name="jdbcTemplate" ref="jdbcTemplate"></property>
    </bean>

    <!--  配置事务处理的拦截器  -->
    <tx:advice id="transactionAdvice">
        <!--  指定不同方法的事务处理策略  -->
        <tx:attributes>
            <tx:method name="insertUser"/>
            <tx:method name="*"/>
        </tx:attributes>
    </tx:advice>

    <!-- 配置AOP Advisor -->
    <aop:config>
        <!--  默认拦截 serviceImpl 的方法      -->
        <aop:pointcut id="defaultServiceOperation" expression="execution(* *ServiceImpl.*(..))"/>
        <aop:advisor pointcut-ref="defaultServiceOperation" advice-ref="transactionAdvice"/>
    </aop:config>
</beans>
```

### 应用逻辑
```
// 接口
public interface UserService {
    void insertUser();

}

// 实现类
public class UserServiceImpl implements UserService{
    // 使用JdbcTemplate操作数据库
    private JdbcTemplate jdbcTemplate;

    // 操作数据库的方法
    public void insertUser() {

        jdbcTemplate.execute("INSERT INTO `task`.`t_user`( `name`) VALUES ('primary');");

        // 模拟异常，抛出异常，事务配置正常的话，对数据库的插入操作会回滚的
        if (true) {
            throw new RuntimeException("执行异常");
        }
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

// 测试类
public class TransactionTestMain {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");

        UserService bean = applicationContext.getBean(UserService.class);

        bean.insertUser();
    }
}
```        

**项目地址** 

项目供参考，具体代码在module **tx_declare_with_xml** 
[github](https://github.com/yuansaysay/spring-aop-practise-handbyhand.git)                  