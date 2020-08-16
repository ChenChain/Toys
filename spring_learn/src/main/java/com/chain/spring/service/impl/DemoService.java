package com.chain.spring.service.impl;

import com.chain.mvcframework.annotation.CHService;

import com.chain.spring.service.IDemoService;

/**
 * @author chenqian091
 * @date 2020-08-16
 */
@CHService
public class DemoService implements IDemoService {
    public String get(String name){
        return "my name is "+ name;
    }

}
