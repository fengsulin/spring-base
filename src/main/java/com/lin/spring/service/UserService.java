package com.lin.spring.service;

import com.lin.spring.anno.Resource;
import com.lin.spring.anno.Component;
import com.lin.spring.anno.Scope;
import com.lin.spring.bean.User;
import com.lin.spring.constant.ScopePolicy;
import com.lin.spring.inter.InitializingBean;

@Component("userService")
@Scope(ScopePolicy.PROTOTYPE)
public class UserService implements InitializingBean {

    @Resource
    private OrderService orderService;

    /**对该字段初始化，希望在bean获取的时候默认给字段负载*/
    private User defaultUser;

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void afterPropertySet() {

    }
}
