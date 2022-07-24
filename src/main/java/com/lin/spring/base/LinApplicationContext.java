package com.lin.spring.base;

import com.lin.spring.anno.Resource;
import com.lin.spring.anno.Component;
import com.lin.spring.anno.ComponentScan;
import com.lin.spring.anno.Scope;
import com.lin.spring.constant.ScopePolicy;
import com.lin.spring.inter.BeanPostProcessor;
import com.lin.spring.inter.InitializingBean;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinApplicationContext {

    /**配置类*/
    private Class configClass;

    /**map集合，存储bean对象*/
    private Map<String,BeanDefinition> beanDefinitionMap = new HashMap<>();

    /**用于存储单例bean*/
    private Map<String,Object> beanSingleMap = new HashMap<>();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    /**构造方法传入一个配置类*/
    public LinApplicationContext(Class configClass) throws ClassNotFoundException {
        this.configClass = configClass;
        // 扫描
        scan(configClass);
        // 遍历beanDefinitionMap，将单例的bean存到beanSingleMap中
        preInstantiateSingletons();
    }

    /**
     * 遍历beanDefinitionMap，将单例的bean存到beanSingleMap中
     */
    private void preInstantiateSingletons(){
        for(Map.Entry<String,BeanDefinition> entry:beanDefinitionMap.entrySet()){
            BeanDefinition b = entry.getValue();
            String beanName = entry.getKey();
            if (b.getScope().equals(ScopePolicy.SINGLE)) {
                Object bean = createBean(beanName, b);
                //存入bean对象
                beanSingleMap.put(beanName,bean);
            }
        }
    }
    /**
     * 创建bean对象
     * @param beanName：bean名称
     * @param beanDefinition：bean定义
     * @return
     */
    private Object createBean(String beanName,BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getClazz();
        Object o = null;
        try {
            o = clazz.newInstance();

            // 依赖注入
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Resource.class)) {
                    String name = field.getName();
                    Object bean = getBean(name);
                    if(bean == null){
                        Resource annotation = field.getAnnotation(Resource.class);
                        if(annotation.required()){
                            throw new IllegalArgumentException();
                        }

                    }
                    field.setAccessible(true);
                    field.set(o,bean);
                }
            }
            // 初始化前操作
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessBeforeInitialization(o,beanName);
            }
            // 初始化
            if(o instanceof InitializingBean){
                ((InitializingBean) o).afterPropertySet();
            }
            // 初始化后操作
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postProcessAfterInitialization(o,beanName);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }

    /**
     * 解析配置类
     * @param configClass
     */
    private void scan(Class configClass){
        if(configClass.isAnnotationPresent(ComponentScan.class)){
            ComponentScan annotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String scanPath = annotation.value();
            scanPath = scanPath.replace(".","/");
            System.out.println(scanPath);
            // 扫描scanPath---》找出Component注解的类---》生产Bean对象
            ClassLoader loader = LinApplicationContext.class.getClassLoader();
            URL resource = loader.getResource(scanPath);
            File file = new File(resource.getFile());
            File[] files = file.listFiles();
            for(File f:files){
                // 如果不是类文件，则跳过本次循环
                if(!f.getName().endsWith(".class")){continue;}
                String absolutePath = f.getAbsolutePath();
                String classAllName = absolutePath.substring(absolutePath.indexOf("classes") + 8, absolutePath.indexOf(".class"));
                System.out.println(classAllName);
                String className = classAllName.replace("\\", ".");
                Class clazz = null;
                try {
                    clazz = loader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(clazz.isAnnotationPresent(Component.class)){
                    if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                        try {
                            BeanPostProcessor beanPo = (BeanPostProcessor) clazz.newInstance();
                            beanPostProcessorList.add(beanPo);
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    Component componentAn = (Component) clazz.getAnnotation(Component.class);
                    String value = componentAn.value();
                    //如果value为空，则默认为类名首字母小写
                    if(value.isEmpty()){
                        String nm = clazz.getName();
                        String[] split = nm.split("\\.");
                        System.out.println(split);
                        String s = split[split.length - 1];
                        char[] chars = s.toCharArray();
                        chars[0] += 32;
                        String s1 = String.valueOf(chars);
                        value = s1;

                    }

                    // 定义一个BeanDefinition
                    BeanDefinition beanDefinition = new BeanDefinition();
                    // 根据Scope注解，判断该类是单例还是多例
                    if(clazz.isAnnotationPresent(Scope.class)){
                        Scope scopeAn = (Scope) clazz.getAnnotation(Scope.class);
                        String scopeValue = scopeAn.value();
                        beanDefinition.setScope(scopeValue);
                    }else {
                        beanDefinition.setScope(ScopePolicy.SINGLE);
                    }
                    beanDefinition.setClazz(clazz);
                    beanDefinitionMap.put(value,beanDefinition);
                }
            }
        }

    }

    /**
     * 获取一个bean
     * @param beanName：bean的名称
     * @return
     */
    public Object getBean(String beanName) throws ClassNotFoundException {
        if(beanDefinitionMap.containsKey(beanName)){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            String scope = beanDefinition.getScope();
            if(ScopePolicy.SINGLE.equals(scope)){
                // 单例
                Object o = beanSingleMap.get(beanName);
                return o;
            }else {
                // 多例,每次调用都会创建一个新的bean对象
                Object bean = createBean(beanName, beanDefinition);
                return bean;
            }
        }else {
            throw new ClassNotFoundException();
        }
    }

}
