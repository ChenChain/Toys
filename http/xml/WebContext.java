package http.xml;

import sax_xml.Entity;
import sax_xml.Mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 上下文--web.xml
 * @author: chain
 * @create: 2020/02/16
 **/
public class WebContext {
    private List<ServletEntity> entities;
    private List<ServletMapping> mappings;
    //servlet与url-pattern对应
    //key--servlet_name value--servlet_class
    private Map<String ,String>entityMap=new HashMap<>();
    //key--url-pattern value--servlet-name
    private Map<String ,String>mappingMap=new HashMap<>();

    public WebContext(List<ServletEntity> entities, List<ServletMapping> mappings) {
        this.entities = entities;
        this.mappings = mappings;
        for (ServletEntity e:entities
             ) {
            entityMap.put(e.getName(),e.getClz());
        }
        for (ServletMapping mapping:mappings){
//            mappingMap.put(mapping.getPatterns(),mapping.getName())
            for (String url:mapping.getUrlPatterns()){
                mappingMap.put(url,mapping.getName());
            }
        }
    }

    /**
     * 根据pattern 找到对应的servlet类
     * @param pattern
     * @return
     */
    public String getClz(String pattern){
        String name=mappingMap.get(pattern);
        return entityMap.get(name);
    }
}
