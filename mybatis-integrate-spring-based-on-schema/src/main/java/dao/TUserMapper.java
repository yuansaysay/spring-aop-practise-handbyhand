package dao;

import model.TUser;

public interface TUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TUser record);

    TUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TUser record);
}