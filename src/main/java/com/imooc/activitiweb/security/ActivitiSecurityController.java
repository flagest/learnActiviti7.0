package com.imooc.activitiweb.security;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jzwu
 * @since 2021/3/1 0001
 */
public class ActivitiSecurityController {

    @RequestMapping("/login")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public String requireAuthentication(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse) {
        return "请先登录，请使用login.html或发起POST登陆请求";
    }
}