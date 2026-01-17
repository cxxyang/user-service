package org.myproject.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.myproject.entry.RegisterDTO;
import org.myproject.entry.SysUser;
import org.myproject.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public List<SysUser> getAllUser() {
        return userMapper.selectList(new QueryWrapper<SysUser>());
    }

    public Long registerUser(RegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在

        // 2. 构造实体对象
        SysUser newUser = new SysUser();
        newUser.setUsername(registerDTO.getUsername());

        // 3. 【核心】对明文密码进行加密
        // 永远不要在数据库存明文密码！BCrypt 会自动处理盐值(salt)
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        newUser.setPassword(encodedPassword);
        newUser.setStatus(1); // 默认启用

        // 4. 存入数据库
        userMapper.insert(newUser);
        return newUser.getId();
    }

    public String login(RegisterDTO registerDTO) {

        return "";
    }
}
