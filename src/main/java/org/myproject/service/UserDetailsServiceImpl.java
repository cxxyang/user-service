package org.myproject.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.myproject.entry.SysUser;
import org.myproject.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询用户
        SysUser sysUser = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );

        // 2. 判断用户是否存在
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }

        // 3. 封装成 Security 的 UserDetails 对象并返回
        // 参数说明：用户名，加密后的密码，权限列表（此处暂给空权限）
        return new User(
                sysUser.getUsername(),
                sysUser.getPassword(),
                sysUser.getStatus() == 1, // 账号是否启用
                true, // 账号未过期
                true, // 凭证未过期
                true, // 账号未锁定
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER") // 赋予默认角色
        );

    }
}
