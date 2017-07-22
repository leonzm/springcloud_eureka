package com.example.springcloud_eureka_client.restful;

import com.example.springcloud_eureka_client.model.User;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

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
    public String hello() throws Exception {
        ServiceInstance serviceInstance = client.getLocalServiceInstance();

        int sleepTime = new Random().nextInt(4000);
        LOGGER.info("sleepTime: " + sleepTime);
        Thread.sleep(sleepTime);

        LOGGER.info("/hello, host: " + serviceInstance.getHost() + ", service_id: " + serviceInstance.getServiceId());
        return "Hello World";
    }

    @RequestMapping(value = "/hello1", method = RequestMethod.GET)
    public String hello(@RequestParam String name) {
        return "Hello " + name;
    }

    @RequestMapping(value = "/hello2", method = RequestMethod.GET)
    public User hello(@RequestHeader String name, @RequestHeader Integer age) {
        return new User(name, age);
    }

    @RequestMapping(value = "/hello3", method = RequestMethod.POST)
    public String hello(@RequestBody User user) {
        return "Hello " + user.getName() + ", " + user.getAge();
    }

}
