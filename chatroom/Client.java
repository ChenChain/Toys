package chatroom;

import java.io.*;
import java.net.Socket;

/**
 * 客户端
 * @author: chain
 * @create: 2020/02/15
 **/
public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("----Client----");

        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入用户名");
        String name=br.readLine();
        Socket client=new Socket("localhost",8888);

        new Thread(new Send(client,name)).start();
        new Thread(new Receive(client)).start();
    }
}
