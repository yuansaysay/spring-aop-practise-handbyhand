import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

public class MyBatisIntegrateSpringTestMain {

    @Test
    public void testMyBatisIntegrateSpring() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");
        UserService bean = applicationContext.getBean(UserService.class);

        bean.insertUser();
    }
}
