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

    @Test
    public void testAdd() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // 这里是编程式使用mybatis的特征,基于JDK动态代理完成sql操作。
        // 在spring 集成 mybatis 的源码中其实有这个特征代码，毕竟要做的事是同一个嘛。
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
