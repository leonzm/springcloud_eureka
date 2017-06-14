package com.example.springcloud_eureka_client.restful;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Leon
 * @CreateDate: 2017/6/14
 * @Description:
 * @Version: 1.0.0
 */
@RestController
public class HelloController {

    private static final Logger LOGGER = Logger.getLogger(HelloController.class);

    @Autowired
    private DiscoveryClient client;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello() {
        ServiceInstance serviceInstance = client.getLocalServiceInstance();
        LOGGER.info("/hello, host: " + serviceInstance.getHost() + ", service_id: " + serviceInstance.getServiceId());
        return "Hello World";
    }

}
