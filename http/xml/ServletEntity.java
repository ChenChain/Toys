package http.xml;

/**
 * xml中servlet的数据结构
 * @author: chain
 * @create: 2020/02/17
 **/
public class ServletEntity {
    private String name;//名字 servlet-name
    private String clz;//类 servlet-class

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClz() {
        return clz;
    }

    public void setClz(String clz) {
        this.clz = clz;
    }
}
