package org.myproject.config;

import org.myproject.filter.JsonLoginFilter;
import org.myproject.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 开启 Web 安全设置
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private MyAuthenticationSuccessHandler successHandler;

    @Autowired
    private MyAuthenticationFailureHandler failureHandler;

    // 配置自定义的过滤器 必须手动注入这个 Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    // 配置自定义的过滤器（支持body为json格式的登录）
    @Bean
    public JsonLoginFilter jsonLoginFilter(AuthenticationManager authenticationManager) {
        JsonLoginFilter filter = new JsonLoginFilter();
        filter.setAuthenticationManager(authenticationManager);
        //loginProcessingUrl 定义的接口是由 过滤器（Filter） 拦截处理的，它并不存在于你的 Controller 中。
        //必须确保这个路径在 antMatchers 中被 permitAll()。 否则，你会陷入“想登录但登录接口要求先登录”的死循环
        filter.setFilterProcessesUrl("/user/login"); // 设置登录接口路径
        // 挂载之前写好的处理器
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);

        return filter;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,JsonLoginFilter jsonLoginFilter) throws Exception {
        http.csrf().disable()//禁用 CSRF (如果是前后端分离项目必须禁用，否则 POST 请求会被拦截)
                .cors()//允许跨域
                .and()
                // 配置请求权限
                .authorizeRequests()
                .antMatchers("/user/login", "/user/register").permitAll() // 允许匿名访问的接口
                .anyRequest().authenticated() // 其他所有请求都需要登录后访问
                .and()
                // 将自定义的 JSON 过滤器插入到原有的 UsernamePasswordAuthenticationFilter 位置
                .addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置登录方式
//                .formLogin()不用表单登录了 用json
//                    .loginProcessingUrl("/user/login")//这里的路径必须在上面的 permitAll 中包含
//                    .successHandler(new MyAuthenticationSuccessHandler()) // 成功处理
//                    .failureHandler(new MyAuthenticationFailureHandler())// 挂载失败处理// 自定义登录页面路径
//                .and()
                // --- 异常处理（处理未登录直接访问接口的情况） ---
                .exceptionHandling()
                .authenticationEntryPoint((req, resp, authEx) -> {
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(401);
                    resp.getWriter().write("{\"code\": 401, \"msg\": \"请先登录\"}");
                });

        // 指定获取用户的数据源
        http.userDetailsService(userDetailsService);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 必须配置加密器，数据库存的必须是加密后的字符串（如 $2a$10...）
        return new BCryptPasswordEncoder();
    }
}
