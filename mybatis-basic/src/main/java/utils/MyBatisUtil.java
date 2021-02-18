package utils;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtil {

    private final static SqlSessionFactory sqlSessionFactory;

    static {
        InputStream inputStream = null;
        try{
            //加载mybatisCfg.xml配置文件，转换成输入流
            inputStream = MyBatisUtil.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
            //根据配置文件的输入流构造一个SQL会话工厂
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        }
        finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

}
