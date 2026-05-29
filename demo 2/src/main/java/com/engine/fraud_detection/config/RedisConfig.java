package com.engine.fraud_detection.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.engine.fraud_detection.model.Transaction;
//Redis cannot store Java objects automatically, so we need to serialize the Java objects into a format that Redis can store (like JSON). 
// The GenericJackson2JsonRedisSerializer is a serializer that uses the Jackson library to convert Java objects to JSON . 
// By setting this serializer for the value of the RedisTemplate, we can store Transaction objects in Redis as JSON strings, and when we retrieve them, they will be deserialized back 
// into Transaction objects.
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//also need to serialize the LocalDateTime attribtue we have in each transaction 
//goal of RedisConfig is to help redis work with Transaction and LocalDateTime objects 
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Transaction> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Transaction> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
            objectMapper.getPolymorphicTypeValidator(),
            ObjectMapper.DefaultTyping.NON_FINAL
        );

        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setKeySerializer(new StringRedisSerializer());

        return template;
    }
}