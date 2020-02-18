package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 客户端
 * @author: chain
 * @create: 2020/02/15
 **/
public class Client3 {
    public static void main(String[] args) throws IOException {

        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入用户名");
        String name=br.readLine();
        Socket client=new Socket("localhost",8888);


        new Thread(new Send(client,name)).start();
        new Thread(new Receive(client)).start();
    }
}
