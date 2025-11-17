package com.university.research.util;

import com.university.research.system.domain.SysUser;
import com.university.research.system.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.boot.test.context.SpringBootTest
public class SpringBootTest {

    @Autowired
    SysUserMapper  sysUserMapper;
    @Test
    void userSelt(){
        SysUser admin = sysUserMapper.selectUserByUsername("admin");
        System.out.println(admin.toString());
    }
}
