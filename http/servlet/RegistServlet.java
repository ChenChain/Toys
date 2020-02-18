package http.servlet;

import http.responserequest.Request;
import http.responserequest.Response;

/**
 * @author: chain
 * @create: 2020/02/17
 **/
public class RegistServlet implements ServletInterface {
    @Override
    public void service() {
        System.out.println("正在使用注册服务...");
    }

    @Override
    public void service(Request request, Response response) {
        response.print("<html>");
        response.print("<head>");
        response.print("<title>");
        response.print("regist_servlet");

        response.print("</title>");
        response.print("</head>");
        response.print("<body>");
        response.print("regist:"+request.getParamValue("uname"));
        response.print("</body>");
        response.print("</html");
        System.out.println("注册成功...");
    }
}
