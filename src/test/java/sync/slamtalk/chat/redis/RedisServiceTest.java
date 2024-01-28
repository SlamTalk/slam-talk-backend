package sync.slamtalk.chat.redis;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@Slf4j
@SpringBootTest
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    @MockBean
    private RedisTemplate<String, String> redisTemplate;

    @MockBean
    private ListOperations<String, String> listOperations;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void saveMessage() {
        String chatRoomId = "room1";
        String message = "Hello";
        String creationTime = "2024-01-28T10:00:00";

        redisService.saveMessage(chatRoomId, message, creationTime);

        verify(listOperations).rightPush("chatRoom:" + chatRoomId, message, creationTime);
    }

    @Test
    void getMessages() {
        String chatRoomId = "room1";
        int start = 0;
        int end = 10;
        List<String> expectedMessages = Arrays.asList("Message1", "Message2");

        when(listOperations.range("chatRoom:" + chatRoomId, start, end)).thenReturn(expectedMessages);

        List<String> actualMessages = redisService.getMessages(chatRoomId, start, end);

        verify(listOperations).range("chatRoom:" + chatRoomId, start, end);
        assert expectedMessages.equals(actualMessages) : "Expected and actual messages do not match";
    }
}