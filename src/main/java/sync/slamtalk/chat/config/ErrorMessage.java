package sync.slamtalk.chat.config;

import jakarta.annotation.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;


public class ErrorMessage extends GenericMessage<Throwable> {
    private static final long serialVersionUID = -1234567895279837728L;

    @Nullable
    private final Message<?> originalMessage;


    // @param : Throwable
    public ErrorMessage(Throwable payload){
        super(payload);
        this.originalMessage = null;
    }

    // @param : Throwable, MessageHeaders
    public ErrorMessage(Throwable payload, MessageHeaders headers){
        super(payload,headers);
        this.originalMessage = null;
    }

    // @param : Throwable, Message<?>
    public ErrorMessage(Throwable payload, Message<?> originalMessage){
        super(payload);
        this.originalMessage = originalMessage;
    }

    // @param : Throwable, MessageHeaders,Message<?>
    public ErrorMessage(Throwable payload, MessageHeaders headers, Message<?> originalMessage) {
        super(payload, headers);
        this.originalMessage = originalMessage;
    }

    @Nullable
    public Message<?> getOriginalMessage(){
        return this.originalMessage;
    }

    @Override
    public String toString() {
        if(this.originalMessage == null){
            return super.toString();
        }
        return super.toString() + "for original " + this.originalMessage;
    }
}
