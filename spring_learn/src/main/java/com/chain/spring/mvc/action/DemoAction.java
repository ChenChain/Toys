package com.chain.spring.mvc.action;

import com.chain.mvcframework.annotation.CHAutowired;
import com.chain.mvcframework.annotation.CHController;
import com.chain.mvcframework.annotation.CHRequestMapping;
import com.chain.mvcframework.annotation.CHRequestParam;
import com.chain.spring.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author chenqian091
 * @date 2020-08-16
 */
@CHController
@CHRequestMapping("/demo")
public class DemoAction {

    @CHAutowired
    private IDemoService demoService;

    public void query(HttpServletRequest req, HttpServletResponse response, @CHRequestParam("name")String name){
        String result=demoService.get(name);
        try {
            response.getWriter().write(result);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @CHRequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp,
                    @CHRequestParam("a") Integer a, @CHRequestParam("b") Integer b){
        try {
            resp.getWriter().write(a + "+" + b + "=" + (a + b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CHRequestMapping("/remove")
    public void remove(HttpServletRequest req,HttpServletResponse resp,
                       @CHRequestParam("id") Integer id){
    }


}
