package http.server;
import http.utils.CloseUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * 使用serversocket 建立连接，获取请求协议
 * @author: chain
 * @create: 2020/02/16
 **/
public class Server {
    private ServerSocket serverSocket;
    private boolean isRunning;
    public static void main(String[] args) {
        Server server=new Server();
        server.start();
    }

    /**
     * 启动服务
     */
    public void start(){
        this.isRunning=true;
        System.out.println("服务启动");
        try {
            serverSocket=new ServerSocket(8888);
            receive();
        } catch (IOException e) {
            System.out.println("服务器启动失败...");
            stop();
        }
    }

    /**
     * 接收连接
     */
    public void receive(){
        while (isRunning){
            try {
                Socket client   =serverSocket.accept();
                System.out.println("一个客户端建立了连接...");
                //获取请求协议
                new Thread(new Dispatcher(client)).start();
            } catch (IOException e) {
                System.out.println("客户端连接出错...");
            }
        }

    }

    /**
     * 关闭服务
     */
    public void stop(){
        this.isRunning=false;
        try {
            this.serverSocket.close();
            System.out.println("服务关闭");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
