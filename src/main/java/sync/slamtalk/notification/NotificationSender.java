package sync.slamtalk.notification;

import sync.slamtalk.notification.dto.request.NotificationRequest;

public interface NotificationSender {
    void send(NotificationRequest request);
}
