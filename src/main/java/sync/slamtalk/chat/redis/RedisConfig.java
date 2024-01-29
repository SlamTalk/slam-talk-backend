package sync.slamtalk.chat.redis;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
@EnableRedisRepositories // Redis 를 사용한다고 명시해주는 어노테이션
public class RedisConfig {

    // Redis 서버와의 연결 정보를 저장하는 객체
    private final RedisProperties redisProperties;


    /*Redis 서버와의 연결을 생성하고 관리하는 역할*/
    // RedisProperties 로 properties 에 저장한 host,port 를 연결
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(redisProperties.getHost(),redisProperties.getPort());
    }

    // serializer 설정으로 redis-cli 를 통해 직접 데이터를 조회할 수 있도록 설정
    @Bean
    public RedisTemplate<String, Object> redisTemplate(){
        RedisTemplate<String ,Object> redisTemplate = new RedisTemplate<>();

        // key 와 value Serializer 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Spring Framework에서 Redis 와의 연결을 생성
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        /* ObjectMapper를 사용하여 Redis의 데이터 직렬화 */
        // Value Serializer 설정
        ObjectMapper objectMapper = new ObjectMapper()
                // 날짜를 문자열 형태로 직렬화
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                //null 값이 없는 필드만 포함
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        serializer.serialize(objectMapper);

        return redisTemplate;
    }
}
