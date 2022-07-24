package com.lin.spring.service;

import com.lin.spring.anno.Component;
import com.lin.spring.anno.Scope;
import com.lin.spring.constant.ScopePolicy;

@Component("userService")
@Scope(ScopePolicy.PROTOTYPE)
public class UserService {
}
