package com.microservices.base.elastic.query.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ElasticQueryServiceApplicationTests {
    @Test
    public void contextLoads(){
        //this test can run without any dependencies when we ru the maven install command. It doesn't have any initialization logic
        //when we run maven install, this class will start a spring context for me, so that I will be sure that spring app/context started without an error.
    }
}
