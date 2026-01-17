package org.myproject.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.myproject.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        response.setContentType("application/json;charset=utf-8");

        // 1. 获取登录成功的用户名
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();

        // 2. 生成 Token
        String token = jwtUtils.createToken(username);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("msg", "登录成功");
        result.put("data", token); // 包含用户名、权限等

        // 使用 Jackson 序列化成 JSON
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}