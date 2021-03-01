package com.imooc.activitiweb.security;


import com.imooc.activitiweb.mapper.UserInfoBeanMapper;
import com.imooc.activitiweb.pojo.UserInfoBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.security.sasl.AuthorizeCallback;

/**
 * @author jzwu
 * @since 2021/3/1 0001
 */
@Configuration
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private UserInfoBeanMapper userInfoBeanMapper;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        String passwrod = passwordEncoder().encode("111");
        UserInfoBean userInfoBean = userInfoBeanMapper.selectByUsername(userName);
        if (StringUtils.isEmpty(userInfoBean)) {
            throw new UsernameNotFoundException("请先注册在登陆");
        }
        return userInfoBean;
   /*     return new User(
                userName
                , passwrod
                , AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ACTIVITI_USER")
        );*/
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}