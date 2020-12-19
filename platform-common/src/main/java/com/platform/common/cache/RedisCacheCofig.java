package com.platform.common.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Created by WangJia on 2016/11/8.
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisCacheCofig  extends CachingConfigurerSupport {

    private final RedisConfigProperties redisConfigProperties;
    /**
     * 默认使用@Cacheable 注解的方法，
     * key 生成策略默认使用的是 参数名+参数值
     * 如果参数名+参数值重复，就会出问题
     * 解决办法：使用自定义缓存策略
     * 类名+方法名+参数值
     *
     * */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object target, Method method, Object... params) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(target.getClass().getName()+"_");
//                sb.append(method.getName()+"_");
//                for (Object obj : params) {
//                    sb.append(obj.toString()+"_");
//                }
//                return sb.toString();
//            }
//        };

        return new DefaultKeyGenerator();
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate<?,?> redisTemplate
            , RedisConnectionFactory redisConnectionFactory){

//        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        cacheManager.setUsePrefix(true);//去掉zset的key记录
//        cacheManager.setDefaultExpiration(7200);//设置默认过期时间 单位 秒
//        return cacheManager;

//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofHours(1)); // 设置缓存有效期一小时
//        return RedisCacheManager
//                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
//                .cacheDefaults(redisCacheConfiguration).build();

        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        //初始化RedisCacheManager
        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, redisCacheConfiguration());
        return cacheManager;
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
                .fromSerializer(jackson2JsonRedisSerializer);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(300))
                .serializeValuesWith(pair);

        return redisCacheConfiguration;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){

        RedisTemplate<String, Object> template = new RedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //非final类型的属性
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(om);
        // 设置value的序列化规则和 key的序列化规则
        template.setKeySerializer(template.getStringSerializer());
        //jackson2JsonRedisSerializer就是JSON序列号规则，
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public JedisPool jedisPool(JedisPoolConfig poolConfig) {

        JedisPool jedisPool = new JedisPool(poolConfig, redisConfigProperties.getHost(), redisConfigProperties.getPort(), redisConfigProperties.getTimeout(), redisConfigProperties.getPassword());
        return jedisPool;
    }

    @Bean
    public JedisPoolConfig poolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.parseInt(redisConfigProperties.getPool().get("max-idle")));
        jedisPoolConfig.setMaxWaitMillis(Integer.parseInt(redisConfigProperties.getPool().get("max-wait")));
        return jedisPoolConfig;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig poolConfig){
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisConfigProperties.getHost());
        redisStandaloneConfiguration.setPassword(redisConfigProperties.getPassword());
        redisStandaloneConfiguration.setPort(redisConfigProperties.getPort());

        redisStandaloneConfiguration.setDatabase(redisConfigProperties.getDatabase());

        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jpcb =
                (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        jpcb.poolConfig(poolConfig);
        //通过构造器来构造jedis客户端配置
        JedisClientConfiguration jedisClientConfiguration = jpcb.build();

        JedisConnectionFactory jcf = new JedisConnectionFactory(redisStandaloneConfiguration,jedisClientConfiguration);
        return jcf;
    }
}
