package org.myproject.config;


import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 为什么不直接抛出异常？
 * 在 Security 的过滤器链中，登录失败的异常会被 AuthenticationFilter 捕获，它不会进入你自定义的 @RestControllerAdvice 全局异常处理器。
 * 因此，必须使用 failureHandler 来处理。
 */
@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        // 设置响应头为 JSON 格式
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置 401 状态码

        String message = "登录失败";

        // 根据异常类型判断具体原因
        if (exception.getMessage().contains("Bad credentials")) {
            message = "用户名或密码错误";
        } else if (exception.getMessage().contains("locked")) {
            message = "账号已被锁定";
        }

        // 返回 JSON 字符串（建议配合你之前的 Result 类）
        response.getWriter().write("{\"code\": 401, \"msg\": \"" + message + "\"}");
    }
}

