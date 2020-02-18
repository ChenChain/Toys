package http.xml;

import http.servlet.ServletInterface;
import org.xml.sax.SAXException;
import sax_xml.servlet.Servlet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 解析WEB-XML文件
 *
 * @author: chain
 * @create: 2020/02/17
 **/
public class WebXmlParse {
    private static WebContext webContext;

    static {
        try {
            //SAX解析
            //获取解析工厂
            SAXParserFactory factory = SAXParserFactory.newInstance();
            //解析器
            SAXParser parser = factory.newSAXParser();
            //加载文档处理
            //编写处理器
            WebHandler handler = new WebHandler();
            File file = new File("F:\\group\\src\\main\\java\\sax_xml\\WEB.xml");//web-xml文件
            parser.parse(file, handler);
            List<ServletEntity> servletEntities = handler.getServletEntities();
            List<ServletMapping> servletMappings = handler.getServletMappings();
            webContext = new WebContext(servletEntities, servletMappings);
        } catch (Exception e) {
            System.out.println("配置文件加载失败");
            e.printStackTrace();
        }

    }


    /**
     * 通过url 获取配置文件的servlet
     *
     * @param url
     * @return
     */
    public static ServletInterface getServletFromUrl(String url) {
        String name = webContext.getClz("/" + url);
        System.out.println(name);
        Class clz = null;
        try {
            clz = Class.forName(name);
        }catch (NullPointerException e){
//            System.out.println("空指针异常");
            return null;
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
            return null;
        }
        ServletInterface servlet = null;
        try {
            servlet = (ServletInterface) clz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return servlet;
//        return null;
    }

}
