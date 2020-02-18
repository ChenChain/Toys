package http.responserequest;
import http.utils.CloseUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: chain
 * @create: 2020/02/16
 **/
public class Response {
    private final String BLANK = " ";
    private final String CRLF = "\r\n";

    private BufferedWriter bufferedWriter;
    //正文
    private StringBuilder content;
    //协议信息
    private StringBuilder headInfo;
    //正文字节数
    private int len = 0;

    public Response() {
        content = new StringBuilder();
        headInfo = new StringBuilder();
        len = 0;
    }

    public Response(Socket client) {
        this();
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        } catch (IOException e) {
            System.out.println("客户端输出构建失败...");
            headInfo=null;
            CloseUtils.release(bufferedWriter,client);
            e.printStackTrace();
        }
    }

    /**
     * 动态添加内容
     * @param 
     */
    public Response print(String info ){
        content.append(info);
        len+=info.getBytes().length;
        return this;
    }
    //带有空行换行
    public Response println(String info ){
        content.append(info).append(CRLF);
        len+=(info+CRLF).getBytes().length;
        return this;
    }


    /**
     * @param status 状态码 200，404...
     */
    private void createHeadInfo(int status) {

        headInfo.append("HTTP/1.1").append(BLANK);
        headInfo.append(status).append(BLANK);
        String status_str="OK";
        switch (status){
            case 200:
                status_str="OK";
                break;
            case 404:
                status_str="NOT FOUND";
                break;
            case 505:
                status_str="SERVER ERROR";
                break;
        }
        headInfo.append(status_str).append(CRLF);

        headInfo.append("Date:").append(new Date()).append(CRLF);
        headInfo.append("Server:").append("chain_test server/0.0.1;charset=utf-8").append(CRLF);
        headInfo.append("Content-type:text/html").append(CRLF);
        headInfo.append("Content-length:").append(len).append(CRLF);
        headInfo.append(CRLF);
        headInfo.append(content.toString());
    }

    //推送响应信息
    public void pushToBrowser(int code) {
        if (null==headInfo){
            code=500;//服务器内部错误 500响应
        }
//        System.out.println(headInfo==null);
        createHeadInfo(code);
        try {
            bufferedWriter.append(headInfo).append(content).flush();
        }catch (IOException E){
            System.out.println("向客户端推送消息异常...");
            CloseUtils.release(bufferedWriter);
        }

    }

    public void setContent(StringBuilder content) {
        this.content = content;
    }
}
