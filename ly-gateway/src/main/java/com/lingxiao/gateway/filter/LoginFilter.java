package com.lingxiao.gateway.filter;

import com.lingxiao.common.CookieUtils;
import com.lingxiao.gateway.config.FilterProperties;
import com.lingxiao.gateway.config.JwtProperties;
import com.lingxiao.utlis.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private FilterProperties filterProperties;

    /**
     * 设置返回值的filter类型
     * @return  pre route post error
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 设定filter的执行顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return 5;
    }

    /**
     * 是否执行该filter
     * @return
     */
    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        List<String> allowPaths = filterProperties.getAllowPaths();
        //判断是否在白名单内
        for (String path: allowPaths) {
            if (requestURI.startsWith(path)){
                return false;
            }
        }
        return true;
    }

    /**
     * 核心执行逻辑   使用公钥进行鉴权，而不是把请求转发到授权中心进行判断  避免过高得网络请求
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            //校验通过，什么都不做
            JwtUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            // 校验出现异常，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}
