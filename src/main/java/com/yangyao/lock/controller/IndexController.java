package com.yangyao.lock.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class IndexController {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    Redisson redisson;

    @RequestMapping("test")
    public String deductStock() {
        String lockKey = "product_001";
        //String clientId = UUID.randomUUID().toString();
        RLock redisLock=redisson.getLock(lockKey);
        String value=null;
        try {
//            Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS);
//            if (!result) {
//                return "error";
//            }
            redisLock.lock();
            String num=stringRedisTemplate.opsForValue().get("number");
            int stock = Integer.parseInt(num);
            if (stock > 0) {
                int realStock = stock - 1;
                stringRedisTemplate.opsForValue().set("number", String.valueOf(realStock));
                System.out.println("扣减成功，剩余库存：" + realStock);
            } else {
                System.out.println("扣减失败，库存不足！");
            }
        } finally {
//            if (clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))){
//                stringRedisTemplate.delete(lockKey);
//            }
            redisLock.unlock();
            return "end";
        }

    }
}
