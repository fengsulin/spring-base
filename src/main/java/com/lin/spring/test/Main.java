package com.lin.spring.test;

import com.lin.spring.base.LinApplicationContext;
import com.lin.spring.config.AppConfig;
import com.lin.spring.service.UserService;

public class Main {
    public static void main (String[] args) throws Exception {
        // 用于spring测试
        LinApplicationContext context = new LinApplicationContext(AppConfig.class);
        UserService userService = (UserService) context.getBean("userService");
        UserService userService2 = (UserService) context.getBean("userService");

        System.out.println(userService);
        System.out.println(userService2);

    }
}
