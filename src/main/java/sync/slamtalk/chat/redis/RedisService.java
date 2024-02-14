package sync.slamtalk.chat.redis;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@AllArgsConstructor
public class RedisService {
    @Autowired
    private final RedisTemplate<String,String> redisTemplate;

    // 메세지 저장
    public void saveMessage(String chatRoomId, String message,String creationTime){
        String key = "chatRoom:"+chatRoomId;
        redisTemplate.opsForList().rightPush(key,message,creationTime);
    }


    // 메세지 가져오기
    public List<String> getMessages(String chatRoomId, int start, int end){
        String key = "chatRoom:"+chatRoomId;
        // 마지막 메세지부터 역순으로 count 만큼의 메세지를 가져온다.
        return redisTemplate.opsForList().range(key,start,end);
    }

    /* 이메일 인증관련 메서드*/
    public String getData(String key) {
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key, String value) {
        ValueOperations<String,String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value);
    }

    public void setDataExpire(String key, String value, long duration) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key, value, expireDuration);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
}
