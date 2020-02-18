package http.servlet;

import http.responserequest.Request;
import http.responserequest.Response;

public interface ServletInterface {
    void service();
    void service(Request request, Response response);
}
