package com.chain.mvcframework.annotation.v3.servlet;

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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chenqian091
 * @date 2020-08-16
 */
public class CHDispatcherServlet extends HttpServlet {

    //保存application.properties配置文件中的内容
    private Properties contextConfig = new Properties();

    //保存扫描到的所有的类名
    private List<String> classNames = new ArrayList<>();

    //url与method对应关系
    //spring中的方式
    private List<Handler> handlerMapping = new ArrayList<>();


    //ioc容器 不考虑并发情况，使用普通map
    private Map<String, Object> ioc = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            //服务器内部出错
            resp.getWriter().write("500 Exception \n" + Arrays.toString(e.getStackTrace()));
        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws IOException, InvocationTargetException, IllegalAccessException {

        Handler handler = getHandler(req);
        if (handler == null) {
            resp.getWriter().write("404 ");
            return;
        }


        //方法形参列表
        Class[] paramTypes = handler.method.getParameterTypes();
        //保存需要自动赋值的参数值
        Object[] paramValues = new Object[paramTypes.length];

        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
            //如果找到匹配的对象，则开始填充参数值
            if (!handler.paramIndexMapping.containsKey(param.getKey())) {
                continue;
            }
            int index = handler.paramIndexMapping.get(param.getKey());
            paramValues[index] = convert(paramTypes[index], value);
        }

        //设置方法中的request和response对象
        int reqIndex = handler.paramIndexMapping.get(HttpServletRequest.class.getName());
        paramValues[reqIndex] = req;
        int respIndex = handler.paramIndexMapping.get(HttpServletResponse.class.getName());
        paramValues[respIndex] = resp;

        Object object = handler.method.invoke(handler.controller, paramValues);

        if (object==null||object instanceof Void){
            return;
        }
        resp.getWriter().write(object.toString());
    }

    /**
     * url传过来的参数都是String类型的，HTTP是基于字符串协议
     * 只需要把String转换为任意类型就好
     */
    private Object convert(Class paramType, String value) {
        if (Integer.class == paramType) {
            return Integer.valueOf(value);
        }
        //如果还有double或者其他类型，继续加if
        //应该用策略模式替代if
        //不实现
        return value;
    }

    private Handler getHandler(HttpServletRequest req) {
        if(handlerMapping.isEmpty()){ return null; }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (Handler handler : handlerMapping) {
            try{
                Matcher matcher = handler.pattern.matcher(url);
                //如果没有匹配上继续下一个匹配
                if(!matcher.matches()){ continue; }

                return handler;
            }catch(Exception e){
                throw e;
            }
        }
        return null;
    }

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));
        //扫描相关类
        doScanner(contextConfig.getProperty("scanPackage"));

        //初始化扫描到的类，并放入IOC容器中
        doInstance();

        //完成依赖注入
        doAutowired();

        //初始化HandlerMapping
        initHandlerMapping();


        System.out.println("CH MVC Framework is inited");
    }

    private void initHandlerMapping() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(CHController.class)) {
                continue;
            }
            String baseUrl = "";
            if (clazz.isAnnotationPresent(CHRequestMapping.class)) {
                CHRequestMapping requestMapping = clazz.getAnnotation(CHRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            for (Method method : clazz.getMethods()) {
                if (!method.isAnnotationPresent(CHRequestMapping.class)) {
                    continue;
                }
                CHRequestMapping requestMapping = method.getAnnotation(CHRequestMapping.class);

                String regex = ("/" + baseUrl + requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new Handler(pattern, entry.getValue(), method));
            }
        }

    }

    private void doAutowired() {
        if (ioc.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {

            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if (!field.isAnnotationPresent(CHAutowired.class)) {
                    continue;
                }
                CHAutowired autowired = field.getAnnotation(CHAutowired.class);
                //如果没有自定义beanName.则按类型注入
                String beanName = autowired.value().trim();

                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                //对非public进行处理
                field.setAccessible(true);

                //赋值 注入
                try {
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private void doInstance() {
        if (classNames.isEmpty()) {
            return;
        }
        try {
            for (String className : classNames) {
                Class<?> clazz = Class.forName(className);
                //加了注解的类就要初始化
                //使用Controller与Service进行操作
                if (clazz.isAnnotationPresent(CHController.class)) {
                    Object instance = clazz.newInstance();
                    //Spring中默认类名首字母小写
                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    //注入IOC容器中
                    ioc.put(beanName, instance);
                } else if (clazz.isAnnotationPresent(CHService.class)) {
                    CHService service = clazz.getAnnotation(CHService.class);
                    String beanName = service.value();
                    if ("".equals(beanName.trim())) {
                        beanName = toLowerFirstCase(beanName);
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);

                    //根据类型自动赋值
                    for (Class<?> i : clazz.getInterfaces()) {
                        if (ioc.containsKey(i.getName())) {
                            throw new Exception("THE " + i.getName() + " IS EXISTING");
                        }
                        //接口类型当作key
                        ioc.put(i.getName(), instance);
                    }
                } else {
                    continue;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 类名首字母小写
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doLoadConfig(String contextConfigLocation) {
        //直接通过类路径找到Spring主配置文件所在的路径
        //并读取内容到Properties中
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 扫描包下的类
     *
     * @param scanPackage 扫描包路径
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName().replace(".class", ""));
            }
            String className = (scanPackage + "." + file.getName()).replace(".class", "");
            classNames.add(className);
        }
    }
}
