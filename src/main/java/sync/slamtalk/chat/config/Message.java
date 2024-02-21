package sync.slamtalk.chat.config;

import org.springframework.messaging.MessageHeaders;

public interface Message<T> {

    T getpayload();

    MessageHeaders getHeaders();
}
