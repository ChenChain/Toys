package com.chain.mvcframework.annotation.v1.servlet;

import com.chain.mvcframework.annotation.CHAutowired;
import com.chain.mvcframework.annotation.CHController;
import com.chain.mvcframework.annotation.CHRequestMapping;
import com.chain.mvcframework.annotation.CHService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author chenqian091
 * @date 2020-08-16
 */
public class CHDispatcherServlet  extends HttpServlet {

    private Map<String ,Object> mapping=new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDispatch(req, resp);
        }catch (Exception e){
            //服务器内部出错
            resp.getWriter().write("500 Exception \n"+ Arrays.toString(e.getStackTrace()));
        }
    }



    private void doDispatch(HttpServletRequest req,HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {
        String url=req.getRequestURI();
        //应用根路径
        String context=req.getContextPath();
        url=url.replace(context,"").replaceAll("/+","/");

        //404
        if (!this.mapping.containsKey(url)){
            resp.getWriter().write("404 NOT FOUND");
            return;
        }

        Map<String,String[]> params=req.getParameterMap();
        Method method= (Method) this.mapping.get(url);
        //对象方法中调用方法
        //取出name的参数，对应该demo中的service方法参数
        method.invoke(this.mapping.get(method.getDeclaringClass().getName()),new Object[]{req,resp,params.get("name")[0]});
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        InputStream is=null;
        try {
            Properties configContext=new Properties();
            is=this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
            configContext.load(is);
            //扫描包路径
            String scanPackage =configContext.getProperty("scanPackage");
            doScanner(scanPackage);

            for (String className:mapping.keySet()) {
                if (!className.contains(".")){
                    continue;
                }
                Class<?> clazz=Class.forName(className);

                //加入容器中
                if (clazz.isAnnotationPresent(CHController.class)){
                    mapping.put(className,clazz.newInstance());
                    String baseUrl="";
                    if (clazz.isAnnotationPresent(CHRequestMapping.class)){
                        //请求对应的方法  前缀字符串
                        CHRequestMapping requestMapping=clazz.getAnnotation(CHRequestMapping.class);
                        baseUrl=requestMapping.value();
                    }
                    Method[] methods=clazz.getMethods();
                    for (Method method:methods) {
                        if (!method.isAnnotationPresent(CHRequestMapping.class)){
                            continue;
                        }
                        CHRequestMapping requestMapping=method.getAnnotation(CHRequestMapping.class);
                        String url=(baseUrl+"/"+requestMapping.value()).replaceAll("/+","/");

                        //请求path 与 方法形成映射
                        mapping.put(url,method);
                    }
                }else if (clazz.isAnnotationPresent(CHService.class)){
                    //为Service
                    CHService service=clazz.getAnnotation(CHService.class);
                    String beanName=service.value();
                    if ("".equals(beanName)){
                        beanName=clazz.getName();
                    }
                    Object instance=clazz.newInstance();
                    //service 注入容器中
                    mapping.put(beanName,instance);
                }else {
                    continue;
                }

            }

            //对容器中的值进行处理
            for (Object o:mapping.values()) {
                if (o==null){
                    continue;
                }
                Class clazz=o.getClass();
                if (clazz.isAnnotationPresent(CHAutowired.class)){
                    Field[] fields=clazz.getDeclaredFields();
                    for (Field field:fields){
                        if (!field.isAnnotationPresent(CHAutowired.class)){
                            continue;
                        }
                        CHAutowired autowired=field.getAnnotation(CHAutowired.class);
                        String beanName=autowired.value();
                        if ("".equals(beanName)){
                            beanName=field.getType().getName();
                        }
                        field.setAccessible(true);
                        try {
                            //字段autowired赋值
                            field.set(mapping.get(clazz.getName()),mapping.get(beanName));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        finally {
            if (is!=null){
                try {
                    is.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        System.out.println("CH MVC Framework is inited");
    }

    /**
     * 扫描包下的类
     * @param scanPackage 扫描包路径
     */
    private void doScanner(String scanPackage){
        URL url=this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classDir=new File(url.getFile());
        for (File file:classDir.listFiles()){
            if (file.isDirectory()){
                doScanner(scanPackage+"."+file.getName().replace(".class",""));
            }
            String className=(scanPackage+"."+file.getName()).replace(".class","");
            mapping.put(className,null);
        }
    }
}
