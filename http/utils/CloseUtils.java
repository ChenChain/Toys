package http.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭流等对象
 * 关闭stream 会对应关闭socket
 * @author: chain
 * @create: 2020/02/17
 **/
public class CloseUtils {
    public static void release(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(closeable + " 关闭失败...");
            }
        }
    }
}
