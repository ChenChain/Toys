package com.chain.esjd.controller;

import com.chain.esjd.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class IndexController {


    @Autowired
    private ContentService contentService;


    /**
     * 普通查询
     * @param keywords
     * @return
     * @throws Exception
     */
    @GetMapping("/parse/{keywords}")
    public Boolean parse( @PathVariable String keywords) throws Exception {
        return   contentService.parseContent(keywords);
    }

    /**
     * 分页查询
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    @GetMapping("/search/{keyword}/{pageNum}/{pageSize}")
    public List<Map<String, Object>> searchByPage(@PathVariable("keyword")  String keyword, @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) throws Exception {
        contentService.parseContent(keyword);
        return contentService.searchPage(keyword, pageNum, pageSize);
    }

    /**
     * 分页高亮查询
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @return
     * @throws IOException
     */
    @GetMapping("/hlsearch/{keyword}/{pageNum}/{pageSize}")
    public List<Map<String, Object>> searchByPageHighLight(@PathVariable("keyword")  String keyword, @PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize) throws Exception {
        contentService.parseContent(keyword);
        return contentService.searchPageHighLight(keyword, pageNum, pageSize);
    }




}
