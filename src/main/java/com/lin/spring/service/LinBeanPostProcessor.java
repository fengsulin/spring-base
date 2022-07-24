package com.lin.spring.service;

import com.lin.spring.anno.Component;
import com.lin.spring.inter.BeanPostProcessor;

@Component
public class LinBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前");
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        return null;
    }
}
