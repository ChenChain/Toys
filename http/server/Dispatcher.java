package http.server;
import http.responserequest.Request;
import http.responserequest.Response;
import http.servlet.ServletInterface;
import http.xml.WebXmlParse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @author: chain
 * @create: 2020/02/17
 **/
public class Dispatcher implements Runnable {
    private Socket client;
    private Request request;
    private Response response;

    public Dispatcher(Socket client) {
        this.client = client;
        try {
            request = new Request(client);
            response = new Response(client);
        } catch (IOException e) {
            e.printStackTrace();
            release();
        }
    }

    @Override
    public void run() {
        if (request.getUrl()==null||request.getUrl().length()==0){
            //到首页
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream("F:\\group\\src\\main\\java\\http\\html\\index.html");
//                File file=new File("F:\\group\\src\\main\\java\\http\\html\\index.html");
//                byte[] bytes=new byte[1024*1024];
//                int len=is.read(bytes);
//                FileOutputStream fileOutputStream=new FileOutputStream(file);
//                fileOutputStream.write(bytes);
//                String content=new String(bytes,0,len);
            String content="<h1>Hello</h1><br/><h1>Welcome, this is the index </h1>";
//                System.out.println(content);
            response.print(content);
            response.pushToBrowser(200);
            return;
        }

        ServletInterface servlet = WebXmlParse.getServletFromUrl(request.getUrl());
        if (servlet != null) {
            //拿到了servlet
            servlet.service(request, response);
            response.print("<h1>servlet deal successfully</h1>");
            response.pushToBrowser(200);
        } else {
            //错误
            System.out.println("404");
//                InputStream is = Thread.currentThread().getContextClassLoader()
//                        .getResourceAsStream("名称");
            response.println("404");
            response.println("There is no any resource about the url ");
            response.pushToBrowser(404);
            //读取错误页面


        }
        release();
    }

    /**
     * 释放client
     */
    private void release() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
