package com.bitcamp.drrate.global.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;


@Configuration
public class RedisConfig {
     @Value("${spring.data.redis.cluster.nodes[0]}")
    private String node1;

    @Value("${spring.data.redis.cluster.nodes[1]}")
    private String node2;

    @Value("${spring.data.redis.cluster.nodes[2]}")
    private String node3;

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        clusterConfiguration.addClusterNode(new RedisNode(node1.split(":")[0], Integer.parseInt(node1.split(":")[1])));
        clusterConfiguration.addClusterNode(new RedisNode(node2.split(":")[0], Integer.parseInt(node2.split(":")[1])));
        clusterConfiguration.addClusterNode(new RedisNode(node3.split(":")[0], Integer.parseInt(node3.split(":")[1])));
        return new LettuceConnectionFactory(clusterConfiguration);
    }
}
