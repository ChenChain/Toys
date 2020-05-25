package com.chain.esjd.service;

import com.alibaba.fastjson.JSON;
import com.chain.esjd.pojo.Content;
import com.chain.esjd.util.HTMLParseUtil;
import org.apache.lucene.index.Term;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.omg.CORBA.TIMEOUT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 数据放入es中
     */
    public Boolean parseContent(String keywords) throws Exception {

        List<Content> contents = new HTMLParseUtil().parseJD(keywords);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        for (Content content : contents
        ) {
            bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(content), XContentType.JSON));
        }

        BulkResponse responses = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !responses.hasFailures();
    }

    /**
     * 获取这些数据实现搜索功能
     */
    public List<Map<String, Object>> searchPage(String keyword, int pageNum, int pageSize) throws IOException {
        if (pageNum <= 1) {
            pageNum = 1;
        }
        //start search
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        //分页
        builder.size(pageSize);
        builder.from(pageNum);

        //精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        builder.query(termQueryBuilder);
        builder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(builder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()
        ) {
            list.add(hit.getSourceAsMap());
        }
        return list;
    }

    /**
     * 高亮搜索
     *
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> searchPageHighLight(String keyword, int pageNum, int pageSize) throws IOException {
        if (pageNum <= 1) {
            pageNum = 1;
        }
        //start search
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder builder = new SearchSourceBuilder();

        //分页
        builder.size(pageSize);
        builder.from(pageNum);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span stype='color:red'  > ");
        highlightBuilder.postTags("</span>");
        builder.highlighter(highlightBuilder);


        //精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        builder.query(termQueryBuilder);
        builder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(builder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //解析结果
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()
        ) {
            //解析高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");

            System.out.println(title);

            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果 需要替换

            System.out.println(sourceAsMap);

//            if (title != null) {
//                Text[] fragments = title.fragments();
//                System.out.println(fragments);
//
//                String n_title = "";
//                for (Text text : fragments) {
//                    n_title += text;
//                    System.out.println("text: "+text +" --- n_title"+ n_title);
//                }
//                //替换原来的内容
//                sourceAsMap.put("title", n_title);
//            }

            sourceAsMap.put("title",title.fragments()[0].toString());

            list.add(sourceAsMap);
            break;
        }
        return list;
    }


}
