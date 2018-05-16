package com.evelyn.mavenplugin.study.launch;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明：TODO
 *
 * @auther by zhaoxl
 * @return <br/>
 * 修改历史：<br/>
 * 1.[2018年05月16日上午19:36]
 */
@RestController
public class TestController {


    @RequestMapping(path = "system/properties",method = RequestMethod.GET)
    public String getSystemProperties() {
        return System.getProperty("boo");
    }
}
