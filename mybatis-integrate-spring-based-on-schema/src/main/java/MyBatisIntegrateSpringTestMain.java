import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.UserService;

public class MyBatisIntegrateSpringTestMain {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("application.xml");

        UserService bean = applicationContext.getBean(UserService.class);

        bean.insertUser();
    }
}
