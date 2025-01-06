package org.mySpring;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationConfigApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap =new ConcurrentHashMap<>();
    private ConcurrentHashMap<String,Object> singletonMap=new ConcurrentHashMap<>();
    public AnnotationConfigApplicationContext(Class configClass) throws Exception {
        this.configClass = configClass;
        scan(configClass);
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("singleton")){
                Object bean = createBean(beanDefinition);
                singletonMap.put(beanName,bean);
            }
        }

    }

    private Object createBean(BeanDefinition beanDefinition) throws InstantiationException, IllegalAccessException {
        return beanDefinition.getClazz().newInstance();
    }

    private void scan(Class configClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();
        ClassLoader classLoader = getClass().getClassLoader();
        String filepath = path.replaceAll("\\.", "/");
        URL resource = classLoader.getResource(filepath);
        File file = null;
        if (resource != null) {
          file = new File(resource.getFile());
        }
        if (file != null) {
            getBeans(file,path,classLoader);
        }
    }

    private void getBeans(File file,String path,ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (file.getName().endsWith(".class")) {
            String tempPath=path.replace(".class","");
            Class<?> clazz = classLoader.loadClass(tempPath);
            BeanDefinition beanDefinition=null;
            if (clazz.isAnnotationPresent(Component.class)) {
                Component declaredAnnotation = clazz.getDeclaredAnnotation(Component.class);
                String beanName = Objects.equals(declaredAnnotation.value(), "") ?clazz.getName().substring(clazz.getName().lastIndexOf(".")+1): declaredAnnotation.value();
                if (clazz.isAnnotationPresent(Scope.class)) {
                    String scope = clazz.getDeclaredAnnotation(Scope.class).value();
                    beanDefinition=new BeanDefinition(clazz,scope);
                }else {
                    beanDefinition=new BeanDefinition(clazz,"singleton");
                }
                beanDefinitionMap.put(beanName,beanDefinition);
            }
        }else if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files) {
                getBeans(f,path+"."+f.getName(),classLoader);
            }
        }
    }
    public Object getBean(String beanName) throws InstantiationException, IllegalAccessException {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                return singletonMap.get(beanName);
            }else {
                return createBean(beanDefinition);
            }
        }else {
           throw new NullPointerException();
        }
    }
}
