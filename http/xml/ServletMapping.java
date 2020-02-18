package http.xml;

import java.util.HashSet;
import java.util.Set;

/**
 * xml中servlet-mapping的数据结构
 *
 * @author: chain
 * @create: 2020/02/17
 **/
public class ServletMapping {
    private String name;//servlet的名字
    private Set<String> urlPatterns;//servlet对应的路径

    public ServletMapping() {
        urlPatterns = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(Set<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public void addPattern(String pattern) {
        this.urlPatterns.add(pattern);
    }
}
