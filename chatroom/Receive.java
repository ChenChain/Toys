package chatroom;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author: chain
 * @create: 2020/02/15
 **/
public class Receive implements Runnable {
    private DataInputStream dis;
    private boolean isRunning;
    private Socket client;

    public Receive(Socket client) {
        this.isRunning=true;

        this.client = client;
        try {
            dis=new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            System.out.println(1);
            release();
        }

    }
    private void release(){
        this.isRunning=false;
        ReleaseUtils.close(dis,client);
    }

    private String recevie(){
        String msg="";
        try {
            msg=dis.readUTF();
        } catch (IOException e) {
            System.out.println(1);

            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public void run() {
        while (isRunning){
            String msg=recevie();
            if (!msg.equals("")){
                System.out.println("接收到： "+ msg);
            }
        }
    }
}
