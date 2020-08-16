package com.chain.mvcframework.annotation.v1.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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



    private void doDispatch(HttpServletRequest req,HttpServletResponse resp){

    }


}
