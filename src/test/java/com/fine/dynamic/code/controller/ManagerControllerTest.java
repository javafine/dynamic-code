package com.fine.dynamic.code.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: Javafine
 * @Description:
 * @Date: Created in 15:38 2022/10/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ManagerControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void execute() throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.getForEntity("/file/sample/function/hello?name=aaa", String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(
                objectMapper.writeValueAsString(response.getBody()),
                objectMapper.writeValueAsString("Hello : aaa")
        );
    }

    @Test
    void getConfig() {
        ResponseEntity<String> response = restTemplate.getForEntity("/conf/get/conf_sample", String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(JSON.parseObject(response.getBody()).getJSONObject("conf_sample").getJSONObject("shape").getString("property"), "rectangle");
    }
}