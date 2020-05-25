package com.chain.esjd.util;

import com.chain.esjd.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 爬取并解析京东商品页面，爬取 图片，标题，价格
 */

@Component
public class HTMLParseUtil {
    /**
     * 爬到jd的数据放到list中
     * @param keywords
     * @return
     * @throws Exception
     */
    public List<Content> parseJD(String keywords) throws Exception {
        String ulr = "https://search.jd.com/Search?keyword=" + keywords;
        Document document = Jsoup.parse(new URL(ulr), 30000);
        Element element = document.getElementById("J_goodsList");

        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");

        List<Content> goodsList=new ArrayList<>();

        //获取li中的内容
        for (Element el : elements
        ) {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            Content content=new Content(title,img,price);
            goodsList.add(content);
        }
        return goodsList;
    }

}
