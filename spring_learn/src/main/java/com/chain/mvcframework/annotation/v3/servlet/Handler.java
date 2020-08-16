package com.chain.mvcframework.annotation.v3.servlet;

import com.chain.mvcframework.annotation.CHRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author chenqian091
 * @date 2020-08-16
 */

public class Handler {
    /**
     * 保存方法对应的实例
     */
    protected  Object controller;

    /**
     * 方法
     */
    protected Method method;

    protected Pattern pattern;

    /**
     * 参数顺序
     */
    protected Map<String,Integer> paramIndexMapping;


    protected Handler(Pattern pattern,Object controller,Method method){
        this.pattern=pattern;
        this.controller=controller;
        this.method=method;
        paramIndexMapping=new HashMap<>();
        putParamIndexMapping(method);
    }

    private void putParamIndexMapping(Method method) {
        //提取方法中加了注解的参数
        Annotation[][] pa=method.getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation a:pa[i]){
                if (a instanceof CHRequestParam){
                    String paramName=((CHRequestParam) a).value();
                    if (!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName,i);
                    }
                }
            }
        }
        //提取方法中的request与response参数
        Class<?> [] paramsTypes=method.getParameterTypes();
        for (int i = 0; i < paramsTypes.length; i++) {
            Class type=paramsTypes[i];
            if (type== HttpServletRequest.class||type== HttpServletResponse.class){
                paramIndexMapping.put(type.getName(),i);
            }
        }
     }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }

    public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
        this.paramIndexMapping = paramIndexMapping;
    }
}
