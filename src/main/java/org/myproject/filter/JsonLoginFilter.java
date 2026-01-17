package org.myproject.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JsonLoginFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 1. 确认是否是 POST 请求
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        // 2. 判断是否是 JSON 请求
        if (request.getContentType().contains("application/json")) {
            try {
                // 3. 解析 JSON 数据
                Map<String, String> loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                String username = loginData.get(getUsernameParameter()); // 默认是 "username"
                String password = loginData.get(getPasswordParameter()); // 默认是 "password"

                if (username == null) username = "";
                if (password == null) password = "";

                // 4. 封装 Token 并提交给 AuthenticationManager 校验
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username.trim(), password);
                setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                throw new AuthenticationServiceException("解析登录 JSON 失败", e);
            }
        }

        // 如果不是 JSON，则走默认的表单处理逻辑
        return super.attemptAuthentication(request, response);
    }
}
