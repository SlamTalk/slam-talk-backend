package sync.slamtalk.chat.service;

public interface ChatNotificationService {

    // 메세지 알림
    void notificationMessage(Long lastMessageId, Long roomId,Long userId);

}
