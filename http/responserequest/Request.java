package http.responserequest;

import http.utils.CloseUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.*;

/**
 * 封装请求协议
 * 获取url method 请求参数
 * @author: chain
 * @create: 2020/02/16
 **/
public class Request {
    final String CRLF="\r\n";

    InputStream stream;
    byte[]bytes;
    //请求信息
    String requestInfo;
    //请求方法
    String method;
    //请求url
    String url;
    //请求参数串
    String queryStr;
    //将请求参数串根据&解析
    Map<String , List<String >> paramsMap;
    public Request(){

    }
    public Request(Socket client) throws IOException {
        paramsMap=new HashMap<>();
        bytes=new byte[1024*1024];
        try {
            stream=client.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("请求初始化失败...");
            CloseUtils.release(stream,client);
        }
        int len=0;
        try {
            len=stream.read(bytes);
            //字符串
            requestInfo=new String(bytes,0,len);
        }catch (Exception e){
            e.printStackTrace();
            requestInfo=null;//出现异常，置request信息为null
            System.out.println("读取request header异常");
            CloseUtils.release(stream,client);
        }
        if (requestInfo!=null){
            parseRequestInfo();
        }
//        CloseUtils.release(stream);//关闭流后 socket也关闭了
    }

    private void parseRequestInfo(){
        System.out.println("开始解析。。。");
//        GET /sada?sd=asd HTTP/1.1
//        Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
        //方法
        this.method=this.requestInfo.substring(0,requestInfo.indexOf("/")).toLowerCase();
        this.method.trim();
        //请求url 可能携带参数
        int startUrl=this.requestInfo.indexOf("/")+1;
        int endUrl=this.requestInfo.indexOf("HTTP/");
        //得到了字符串URL
        this.url=requestInfo.substring(startUrl,endUrl).trim();
        //获取参数问号？
        int queryIndex=url.indexOf("?");
        if (queryIndex>=0){
            //存在请求参数
            String[] urlArr=this.url.split("\\?");
            this.url=urlArr[0];
            if (urlArr.length>1){
                queryStr=urlArr[1];
            }

        }
        //获取URL中的参数 分get与post
        if (method.equals("post")){
            String postStr=this.requestInfo.substring(requestInfo.lastIndexOf(CRLF)).trim();
            if (null==queryStr){
                queryStr=postStr;
            }else {
                queryStr+="&"+postStr;
            }
        }
        if (null==queryStr){
            queryStr="";
        }
        System.out.println(method+"-->"+url+"-->"+queryStr);
        //将字符串分解
        //a=1&a=2&a=3&b=1...
        strConvertMap();
    }

    /**
     * 将参数串转换成map
     */
    private void strConvertMap(){
        String[] keyValue=queryStr.split("&");
        for (String query:keyValue){
            // 依据 = 分割
            String[] keyVa=queryStr.split("=");
            keyVa= Arrays.copyOf(keyVa,2);
            String key=keyVa[0];
            String val=keyVa[1]==null?null:decode(keyVa[1],"UTF-8").trim();
            if (!paramsMap.containsKey(key)){
                paramsMap.put(key,new ArrayList<>());
            }
            paramsMap.get(key).add(val);
        }
    }

    /**
     * 处理中文
     * 对中文编码
     * @param v
     * @param enc
     * @return
     */
    private String  decode(String v,String enc){
        try {
            return URLDecoder.decode(v, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.out.println("处理中文编码异常...");
        }
        return null;
    }

    /**
     * 从Map中依据key得到参数数组
     * @param key
     * @return
     */
    public String[] getParamValues(String key){
        List<String > values=paramsMap.get(key);
        if (values==null||values.size()<1){
            return null;
        }
        return values.toArray(new String[0]);
    }
    //获取一个值
    public String getParamValue(String key){
        String[] values=getParamValues(key);
        return values==null?null:values[0];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getQueryStr() {
        return queryStr;
    }

    public Map<String, List<String>> getParamsMap() {
        return paramsMap;
    }
}
