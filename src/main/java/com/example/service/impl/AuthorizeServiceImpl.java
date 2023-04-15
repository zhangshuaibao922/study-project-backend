package com.example.service.impl;

import com.example.entity.Account;
import com.example.mapper.AccountMapper;
import com.example.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AuthorizeServiceImpl implements AuthorizeService {
    @Value("${spring.mail.username}")
    String from;
    @Resource
    AccountMapper accountMapper;
    @Resource
    StringRedisTemplate template;

    @Resource
    MailSender mailSender;

    BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("用户名不能为空");
        }
        Account account = accountMapper.findAccountByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }

    /**
     * 1.生成对应的验证码
     * 2.把邮箱和对应的验证码放到Redis里面，过期时间3分钟，
     * 如果此时重新要求发邮箱，只要剩余时间低于两分钟，就可以重新发送一次，重复
     * 3.发送验证码到指定邮箱中
     * 4.发送失败，把redis里面的刚刚插入的删除
     * 5.用户在注册时，再从redis里面取出对应的键值对，验证
     *
     * @param email
     * @param id
     * @return
     */
    @Override
    public String sendValidateEmail(String email, String id) {
        String key = "email:"+id+":"+email;
        if(Boolean.TRUE.equals(template.hasKey(key))){
            Long expire = Optional.ofNullable(template.getExpire(key, TimeUnit.SECONDS)).orElse(0L);
            if(expire>120){
                return "请求频繁，请稍后再试";
            }
        }
        if(accountMapper.findAccountByNameOrEmail(email)!=null){
            return "邮箱已注册";
        }
        Random random = new Random();
        int code = random.nextInt(899999)+100000;
        SimpleMailMessage messagees = new SimpleMailMessage();
        messagees.setFrom(from);
        messagees.setTo(email);
        messagees.setSubject("您的验证邮件");
        messagees.setText("验证码是："+code+"有效时间为3分钟");
        try{
            mailSender.send(messagees);
            template.opsForValue().set(key, String.valueOf(code),3, TimeUnit.MINUTES);
            return null;
        }catch (MailException e){
            e.printStackTrace();
        }
        return "邮箱发送失败，请检查邮箱";
    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        String key = "email:"+sessionId+":"+email;
        if(Boolean.TRUE.equals(template.hasKey(key))){
            String s = template.opsForValue().get(key);
            if(s==null){
                return "验证码失效，请重新请求";
            }
            if(s.equals(code)){
                password=encoder.encode(password);
                int account = accountMapper.createAccount(username, password, email);
                if(account>0){
                    return null;
                }else {
                    return "内部错误，请联系管理员";
                }
            }else {
                return "验证码错误";
            }
        }else {
            return "请先发送验证码";
        }
    }
}
