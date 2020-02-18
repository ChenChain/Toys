package chatroom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 聊天室服务端
 *
 * @author: chain
 * @create: 2020/02/15
 **/
public class Chat {
    private static CopyOnWriteArrayList<Channel> all = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println("-----Server------");
        ServerSocket server = new ServerSocket(8888);

        while (true) {
            Socket client = server.accept();
            System.out.println("一个客户端进行连接");
            Channel c = new Channel(client);
            all.add(c);
            new Thread(c).start();
        }
    }

    static class Channel implements Runnable {
        private DataOutputStream dos;
        private DataInputStream dis;
        private Socket client;
        private boolean isRunning;
        private String name;
        public Channel() {
        }

        public Channel(Socket client) {
            this.client = client;
            this.isRunning = true;
            try {
                this.dis = new DataInputStream(client.getInputStream());
                this.dos = new DataOutputStream(client.getOutputStream());
               this.name=receive();
               //欢迎
                this.send("欢迎到来");
                sendOther(name+"来到了聊天室",true);
            } catch (Exception e) {
                System.out.println("初始化出错");
                release();
            }

        }

        //接收消息
        //发送消息
        //释放资源
        private String receive() {
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                System.out.println("接收出错");
                release();
            }
            return msg;
        }

        private void send(String msg) {
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (Exception e) {
                System.out.println("发送出错");
                release();
            }
        }


        /**
         * 私聊约定数据格式：@xxx：msg
         * @param msg
         * @param isFromSystem
         */
        //输出信息到所有客户端
        private void sendOther(String msg,boolean isFromSystem) {

            boolean isPrivate=msg.startsWith("@");
            if(isPrivate){
                int idx=msg.indexOf(":");
                //私聊 获取私聊目标
                String targetName=msg.substring(1,idx);
                msg=msg.substring(idx+1);
                for (Channel c:all){
                    if (c.name.equals(targetName)){
                        c.send(this.name+"悄悄对你说： "+ msg);
                    }
                }
            }else {
                for (Channel c : all) {
                    if (c == this) {
                        //自身
                        continue;
                    } else {
                        if (!isFromSystem) {
                            c.send(this.name + "对所有人说：" + msg);
                        }else {
                            c.send(msg);
                        }
                    }
                }
            }



        }


        private void release() {
            this.isRunning = false;
            ReleaseUtils.close(dis, dos, client);
            all.remove(client);
            sendOther(this.name+ "离开了",true);
        }

        @Override
        public void run() {
            while (isRunning) {
                String msg = receive();
                if (!msg.equals("")) {
                    send("我："+msg);
                    sendOther(msg,false);
                } else {
                    send("as");
                }
            }
        }
    }


}
