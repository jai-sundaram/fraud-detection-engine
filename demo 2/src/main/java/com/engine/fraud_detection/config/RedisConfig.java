package com.engine.fraud_detection.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import com.engine.fraud_detection.model.Transaction;
//Redis cannot store Java objects automatically, so we need to serialize the Java objects into a format that Redis can store (like JSON). 
// The GenericJackson2JsonRedisSerializer is a serializer that uses the Jackson library to convert Java objects to JSON . 
// By setting this serializer for the value of the RedisTemplate, we can store Transaction objects in Redis as JSON strings, and when we retrieve them, they will be deserialized back 
// into Transaction objects.

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Transaction> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Transaction> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setValueSerializer(
            new GenericJackson2JsonRedisSerializer()
        );

        return template;
    }
}