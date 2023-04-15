package com.example.mapper;

import com.example.entity.Account;
import com.example.entity.AccountUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountMapper {
    @Select("select * from db_account where username = #{text} or email = #{text}")
    Account findAccountByNameOrEmail(String text);
    @Select("select * from db_account where username = #{text} or email = #{text}")
    AccountUser findAccountUserByNameOrEmail(String text);
    @Insert("insert into db_account (username,password,email) values (#{username},#{password},#{email})")
    int createAccount(String username,String password,String email);
    @Update("update db_account set password = #{password} where email = #{email}")
    int resetPasswordByEmailInt(String password,String email);
}
