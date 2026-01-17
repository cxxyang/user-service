package org.myproject.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user")
public class SysUser {

        @TableId(type = IdType.ASSIGN_ID)
        private Long id;
        private String username;
        private String password;
        private Integer status; // 0-禁用，1-启用
}
