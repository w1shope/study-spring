package com.example.devops.redis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisCrudTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    final String KEY = "MY_KEY";
    final String VALUE = "MY_VALUE";

    @Test
    @DisplayName("Redis CRUD Test")
    void crudTest() {

        redisTemplate.opsForValue().set(KEY, VALUE); // 저장
        String value1 = redisTemplate.opsForValue().get(KEY);// 조회
        redisTemplate.delete(KEY); // 삭제
        String value2 = redisTemplate.opsForValue().get(KEY); // 조회

        assertThat(value1).isEqualTo(VALUE); // 검증
        assertThat(value2).isNull(); // 검증

    }
}
