package org.myApp;

import org.mySpring.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext myContext=new AnnotationConfigApplicationContext(AppConfig.class);
        Object testService = myContext.getBean("TestService");
        System.out.println(testService);
        Object testService1 = myContext.getBean("TestService");
        System.out.println(testService1);
        Object testService2 = myContext.getBean("TestService1");
        System.out.println(testService2);
        Object testService3 = myContext.getBean("TestService1");
        System.out.println(testService3);
    }
}