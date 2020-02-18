package http.servlet;

import http.responserequest.Request;
import http.responserequest.Response;

/**
 * @author: chain
 * @create: 2020/02/17
 **/
public class LoginServlet implements ServletInterface {
    @Override
    public void service() {
        System.out.println("正在使用登陆服务...");
    }

    @Override
    public void service(Request request, Response response) {
        response.print("<html>");
        response.print("<head>");
        response.print("<title>");
        response.print("login_servlet");

        response.print("</title>");
        response.print("</head>");
        response.print("<body>");
        response.print("login:"+request.getParamValue("uname"));
        response.print("</body>");
        response.print("</html");
    }
}
