package sync.slamtalk.chat.config;

import jakarta.annotation.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

public class GenericMessage<T> implements Message<T>, Serializable {
    private static final long serialVersionUID = 1234567892358035098L; // 알아보기..

    // 실제데이터
    private final T payload;

    // 헤더
    private final MessageHeaders headers;

    public GenericMessage(T payload){
        this(payload,new MessageHeaders(null));
    }

    // 생성자
    public GenericMessage(T payload, MessageHeaders headers){
        // payload == null 이면 오류 발생
        Assert.notNull(payload,"Payload must not be null");
        // header == null 이면 오류 발생
        Assert.notNull(headers,"MessageHeaders must not be null");
        this.payload = payload;
        this.headers = headers;
    }

    // 동등성 확인
    @Override
    public boolean equals(@Nullable Object other) {
        if(this==other){
            return true;
        }
        if(!(other instanceof GenericMessage)){
            return false;
        }
        GenericMessage<?> otherMsg = (GenericMessage<?>) other;

        // 적절한 배열 같은 비교를 위해 사용
        return (ObjectUtils.nullSafeEquals(this.payload,otherMsg.payload) && this.headers.equals((otherMsg.headers)));
    }

    // 실제 데이터 반환
    @Override
    public T getpayload() {
        return this.payload;
    }

    // 헤더값 반환
    @Override
    public MessageHeaders getHeaders() {
        return this.headers;
    }

    // 메세지 문자열 변환
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" [payload=");
        if(this.payload instanceof byte[]){
            sb.append("byte[").append(((byte[])this.payload).length).append(']');
        }else{
            sb.append(this.payload);
        }
        sb.append(", headers =").append(this.headers).append(']');
        return sb.toString();
    }
}
