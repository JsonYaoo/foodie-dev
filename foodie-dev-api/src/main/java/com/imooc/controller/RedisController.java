package com.imooc.controller;

import com.imooc.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApiIgnore
@RestController
@RequestMapping("/redis")
public class RedisController {

//    @Autowired
//    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key, String value){
//        redisTemplate.opsForValue().set(key, value);
        redisOperator.set(key, value);
        return "Set OK~";
    }

    @GetMapping("/get")
    public Object get(String key){
//      return (String) redisTemplate.opsForValue().get(key);
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key){
//        redisTemplate.delete(key);
        redisOperator.del(key);
        return "Delete OK~";
    }

    /**
     * 批量从redis中获取key
     * @param keys
     * @return
     */
    @GetMapping("/getALot")
    public Object getALot(String... keys){
        List<String> result = new ArrayList<>();
        // 循环查询
//        for(String key : keys){
//            result.add(redisOperator.get(key));
//        }

        // mget查询
//        result = redisOperator.mget(Arrays.asList(keys));

        // 管道查询
        List<Object> result2 = redisOperator.batchGet(Arrays.asList(keys));

        // 返回JsonList类型数据
        return result2;
    }
}
