package com.imooc.jvm.jvm.action;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法区溢出测试: 元空间溢出测试: -XX:MetaspaceSize=10m -XX:MaxMetaspaceSize=10m
 * => java.lang.OutOfMemoryError: Metaspace, 由于类信息是放在元空间, 而这里的CGLIB动态代理不断地生成Class, 且设置了元空间大小最大限制, 所以一会就溢出了
 * => 而JDK7可能会报持久代溢出的异常
 */
public class MethodAreaOOMTest2 {
    /**
     * CGLib：https://blog.csdn.net/yaomingyang/article/details/82762697
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Hello.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                    System.out.println("Enhanced hello");
                    // 调用Hello.say()
                    return proxy.invokeSuper(obj, args);
                }
            });
            Hello enhancedOOMObject = (Hello) enhancer.create();
            enhancedOOMObject.say();
            System.out.println(enhancedOOMObject.getClass().getName());
        }
    }
}

class Hello {
    public void say() {
        System.out.println("Hello Student");
    }
}