import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public class UserServiceImpl implements UserService{

    private JdbcTemplate jdbcTemplate;

    public void insertUser() {

        jdbcTemplate.execute("INSERT INTO `t_user`( `name`) VALUES ('primary');");

        // 模拟异常，抛出异常，事务配置正常的话，对数据库的插入操作会回滚的
        if (true) {
            throw new RuntimeException("执行异常");
        }
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
