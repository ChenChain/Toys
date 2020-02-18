package chatroom;

import java.io.*;
import java.net.Socket;

/**
 * @author: chain
 * @create: 2020/02/15
 **/
public class Send implements Runnable {
    BufferedReader console;

    DataOutputStream dos;
    String name;
    private boolean isRunning;
    private Socket client;
    public Send(Socket client,String name){
        this.isRunning=true;
        this.name=name;
        this.client=client;
        console=new BufferedReader(new InputStreamReader(System.in));
        try {
            dos=new DataOutputStream(client.getOutputStream());
            //发送名称
            send(name);
        } catch (IOException e) {
            release();
            System.out.println(1);
        }
    }

    private void release(){
        this.isRunning=false;
        ReleaseUtils.close(dos,client);
    }


    private String  getStrFromConsole(){
        try {
            return console.readLine();
        } catch (IOException e) {
            release();
            System.out.println(2);
        }
        return "";
    }

    private void send(String  msg){
        try {
            if (!msg.equals(""))
            {
                dos.writeUTF(msg);
                dos.flush();
            }
        }catch (Exception e){
            release();
            System.out.println(3);
        }
    }

    @Override
    public void run() {
        while (isRunning){
            String msg=getStrFromConsole();
            if (!msg.equals("")){
                send(msg);
            }
        }
    }
}
