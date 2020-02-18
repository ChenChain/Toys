package chatroom;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author: chain
 * @create: 2020/02/15
 **/
public class ReleaseUtils {

    /**
     * 释放资源
     * @param targets
     */
    public static void close(Closeable ...targets){
        for (Closeable closeable: targets){
            try {
                if (closeable!=null){
                    closeable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
