package sync.slamtalk.notification;

import sync.slamtalk.notification.dto.request.ChatNotificationRequest;
import sync.slamtalk.notification.dto.request.NotificationRequest;

public interface NotificationSender {
    /**
     * Notification 생성
     * @param request
     */
    void send(NotificationRequest request);

    /**
     * ChatNotification 생성
     * @param request
     */
    void send(ChatNotificationRequest request);
}
