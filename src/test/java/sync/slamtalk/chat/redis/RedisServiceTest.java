package sync.slamtalk.chat.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataRedisTest
@Import(RedisService.class) // MessageService를 테스트 컨텍스트에 등록
public class MessageServiceTest {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate stringRedisTemplate;

    @Test
    void testSaveMessageWithExpiration() throws InterruptedException {
        String chatRoomId = "1";
        String messageId = "testMessageId";
        String messageContent = "Hello Redis";
        long timeoutInSeconds = 1; // 1초 후 만료

        redisService.saveMessage(chatRoomId, messageId, messageContent, timeoutInSeconds);

        // 메시지가 저장되었는지 확인
        String storedMessage = redisService.getMessage(chatRoomId, messageId);
        assertThat(storedMessage).isEqualTo(messageContent);

        // 설정한 만료 시간 후에 메시지가 삭제되었는지 확인
        TimeUnit.SECONDS.sleep(timeoutInSeconds + 1); // 만료 시간보다 조금 더 기다림
        storedMessage = redisService.getMessage(chatRoomId, messageId);
        assertThat(storedMessage).isNull();
    }
}