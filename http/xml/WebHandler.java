package http.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析xml方法的类
 * @author: chain
 * @create: 2020/02/17
 **/
public class WebHandler extends DefaultHandler {

    private List<ServletEntity> servletEntities;
    private List<ServletMapping> servletMappings;
    private ServletEntity servletEntity;
    private ServletMapping servletMapping;
    private String  tag;//元素的名称
    private boolean isMapping;

    public WebHandler(){}


    @Override
    public void startDocument() throws SAXException {
        servletEntities=new ArrayList<>();
        servletMappings=new ArrayList<>();
    }

    @Override
    public void endDocument() throws SAXException {
        //文档解析结束
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName!=null){
            tag=qName;
            if (qName.equals("servlet")){
                servletEntity=new ServletEntity();
                isMapping=false;
            }else if (qName.equals("servlet-mapping")){
                servletMapping=new ServletMapping();
                isMapping=true;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String content=new String(ch,start,length).trim();
        if (tag!=null){
            if (isMapping){
                //针对servlet-mapping
                if (tag.equals("servlet-name")) {
                    servletMapping.setName(content);
                }else if(tag.equals("url-pattern")){
                    servletMapping.addPattern(content);
                }
            }else {
                //针对servlet
                if (tag.equals("servlet-name")) {
                    servletEntity.setName(content);
                }else if(tag.equals("servlet-class")){
                    servletEntity.setClz(content);
                }
            }
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        tag=null;
        if (qName!=null){
            if (qName.equals("servlet")){
                servletEntities.add(servletEntity);
            }else if (qName.equals("servlet-mapping")){
                servletMappings.add(servletMapping);
            }
        }
    }

    public List<ServletEntity> getServletEntities() {
        return servletEntities;
    }

    public void setServletEntities(List<ServletEntity> servletEntities) {
        this.servletEntities = servletEntities;
    }

    public List<ServletMapping> getServletMappings() {
        return servletMappings;
    }

    public void setServletMappings(List<ServletMapping> servletMappings) {
        this.servletMappings = servletMappings;
    }

    public ServletEntity getServletEntity() {
        return servletEntity;
    }

    public void setServletEntity(ServletEntity servletEntity) {
        this.servletEntity = servletEntity;
    }

    public ServletMapping getServletMapping() {
        return servletMapping;
    }

    public void setServletMapping(ServletMapping servletMapping) {
        this.servletMapping = servletMapping;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isMapping() {
        return isMapping;
    }

    public void setMapping(boolean mapping) {
        isMapping = mapping;
    }
}
