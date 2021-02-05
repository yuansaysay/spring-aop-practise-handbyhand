**完全基于 xml 配置的方式，集成 Mybatis demo**
> 目标：
>
> 1.熟悉mybatis的集成方式，组件配置细节，磨刀不误砍柴工
>
> 2.根据配置细节，顺便就学习一波mybatis的实现原理了，学习方式已有认知+猜测+debug

### 必要前置基础认知
1.**所谓集成到spring，其实就是将 mybatis 相关的 bean，纳入spring的容器内进行管理**，这里用到的是IOC功能。
所以，我们需要配置一下 bean
例如：
- 负责管理连接的 SqlSessionFactoryBean
- Mapper 接口的实例bean

2.我们定义的**Mapper是**interface,并没有具体逻辑？
那么逻辑是怎么产生并执行的呢？
其实这里是**用JDK的动态代理实现的**，动态的生成Mapper的实现类，来执行我们在xml中配置的sql。

### 核心配置详解
集成mybatis的核心配置
**注意关键点：**
- SqlSessionFactoryBean
- MapperFactoryBean
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/aop
        http://www.springframework.org/schema/aop/spring-aop-3.0.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!-- 加载jdbc.properties文件 -->
    <context:property-placeholder location="classpath:application.properties"/>

    <!--  ===================================================================================   数据连接核心配置  -->

    <!-- 配置连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <property name="driverClass" value="${spring.datasource.current.driver-class-name}"></property>
        <property name="jdbcUrl" value="${spring.datasource.current.jdbc-url}"></property>
        <property name="user" value="${spring.datasource.current.username}"></property>
        <property name="password" value="${spring.datasource.current.password}"></property>
    </bean>

    <!--  ===================================================================================   Spring 事务核心配置  -->

    <!-- 配置事务核心管理器,封装了所有的事务操作,依赖于连接池 -->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"></property>
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
        <aop:pointcut id="defaultServiceOperation" expression="execution(* *..*ServiceImpl.*(..))"/>
        <aop:advisor pointcut-ref="defaultServiceOperation" advice-ref="transactionAdvice"/>
    </aop:config>

    <!--  ===================================================================================   Mybatis 核心配置  -->
    <!-- 配置SqlSessionFactory -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 注入dataSource -->
        <property name="dataSource" ref="dataSource"/>
        <!-- mybatis批量别名配置 -->
        <property name="typeAliasesPackage" value="model"/>

        <property name="mapperLocations" value="classpath:mapper/*.xml"></property>
    </bean>

    <!--  配置Mapper的两种方式-->
    <!--    &lt;!&ndash; 配置Mapper代理对象方式一：MapperFactoryBean（了解） &ndash;&gt;-->
    <!--    &lt;!&ndash; 通过MapperFactoryBean生成的代理对象，一次只能针对一个接口进行生成 &ndash;&gt;-->
    <!--    <bean id="TUserMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">-->
    <!--        <property name="sqlSessionFactory" ref="sqlSessionFactory"></property>-->
    <!--        <property name="mapperInterface" value="dao.TUserMapper"></property>-->
    <!--    </bean>-->

    <!--    &lt;!&ndash; 配置Mapper代理对象方式二：MapperScannerConfigurer &ndash;&gt;-->
    <!--    &lt;!&ndash; 批量代理对象的生成 &ndash;&gt;-->
    <!--    &lt;!&ndash; 注意事项：mapper接口类和mapper映射文件同包同名 &ndash;&gt;-->
    <!--    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">-->
    <!--        &lt;!&ndash; 指定需要生成代理的接口所在的包名 &ndash;&gt;-->
    <!--        <property name="basePackage" value="dao"/>-->
    <!--        &lt;!&ndash; 注意事项：通过 sqlSessionFactoryBeanName 配置 sqlSessionFactory &ndash;&gt;-->
    <!--        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>-->
    <!--    </bean>-->

    <!--  这里由于完全基于xml配置，所以采用通过FactoryBean手动配置Mapper  -->
    <bean id="tUserMapper" class="org.mybatis.spring.mapper.MapperFactoryBean">
        <!--sqlSessionFactory属性指定要用到的SqlSessionFactory实例-->
        <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
        <!--mapperInterface属性指定映射器接口，用于实现此接口并生成映射器对象-->
        <property name="mapperInterface" value="dao.TUserMapper"/>
    </bean>


    <!--  ===================================================================================   业务组件配置  -->

    <bean id="userService" class="service.UserServiceImpl">
        <property name="tUserMapper" ref="tUserMapper"></property>
    </bean>


</beans>
```

