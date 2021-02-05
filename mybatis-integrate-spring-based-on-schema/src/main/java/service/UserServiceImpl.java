package service;

import dao.TUserMapper;
import model.TUser;

public class UserServiceImpl implements UserService {

    private TUserMapper tUserMapper;

    public void insertUser() {

        TUser tUser = new TUser();
        tUser.setName("name");
        tUserMapper.insertSelective(tUser);

        // 模拟异常，抛出异常，事务配置正常的话，对数据库的插入操作会回滚的
        if (true) {
            throw new RuntimeException("执行异常");
        }
    }


    public void settUserMapper(TUserMapper tUserMapper) {
        this.tUserMapper = tUserMapper;
    }
}
