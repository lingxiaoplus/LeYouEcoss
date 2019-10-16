package com.lingxiao.interceptor;

import com.lingxiao.common.CookieUtils;
import com.lingxiao.config.JwtProperties;
import com.lingxiao.pojo.UserInfo;
import com.lingxiao.utlis.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginInterceptor extends HandlerInterceptorAdapter {

    private JwtProperties jwtProperties;
    private static ThreadLocal<UserInfo> threadLocal = new ThreadLocal<>();  //线程域，用于存放用户信息

    public LoginInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        if (StringUtils.isBlank(token)){
            //未登陆，返回
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            threadLocal.set(userInfo);
            //鉴权成功，放行
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        //视图渲染完成，不需要user对象了
        threadLocal.remove();
    }


    public static UserInfo getUserInfo(){
        return threadLocal.get();
    }

}
