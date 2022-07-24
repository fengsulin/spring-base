package com.lin.spring.service;

import com.lin.spring.anno.Resource;
import com.lin.spring.anno.Component;
import com.lin.spring.anno.Scope;
import com.lin.spring.constant.ScopePolicy;

@Component("userService")
@Scope(ScopePolicy.PROTOTYPE)
public class UserService {

    @Resource
    private OrderService orderService;

    public void test(){
        System.out.println(orderService);
    }
}
