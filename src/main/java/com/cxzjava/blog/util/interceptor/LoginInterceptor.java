package com.cxzjava.blog.util.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 登录拦截器
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    /**
     *
     * @param request  HttpServletRequest对象代表客户端的请求，当客户端通过HTTP协议访问服务器时，
     *                 HTTP请求头中的所有信息都封装在这个对象中，通过这个对象提供的方法，可以获得客户端请求的所有信息。
     *                 即封装HTTP请求消息的HttpServletRequest对象
     * @param response  代表Http响应消息的HttpServletResponse对象
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if(request.getSession().getAttribute("user") == null){
            response.sendRedirect("/admin");
            return false;
        }else{
            return true;
        }
    }
}
