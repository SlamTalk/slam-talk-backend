package sync.slamtalk.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.chat.entity.UserChatRoom;
import sync.slamtalk.chat.repository.UserChatRoomRepository;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.ChatNotificationRequest;
import sync.slamtalk.notification.model.Notification;
import sync.slamtalk.notification.model.NotificationType;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatNotificationServiceImpl implements ChatNotificationService{

    private final UserChatRoomRepository userChatRoomRepository;
    private final MessageService messageService;
    private final NotificationSender notificationSender;


    @Override
    @Transactional
    public void notificationMessage(Long lastMessageId, Long roomId, Long userId) {

        log.debug("=== notificationMessage 들어왔따 ===");

        // 새로운 메세지가 발생한 채팅방
        List<UserChatRoom> userChatRooms = userChatRoomRepository.findByChat_Id(roomId);
        for(UserChatRoom u : userChatRooms){
            // 메세지를 보낸 유저는 알림 생성안함
            if(u.getUser().getId().equals(userId)){
                continue;
            }
            // userChatRoom 의 readIndex 보다 채팅방에 도착한 마지막 메세지 아이디가 더 큰 경우 -> 새로운 메세지 알림
            boolean shouldNotify = u.getReadIndex() < lastMessageId || u.getNotifications().isEmpty();
            if(shouldNotify){
                log.debug("=== 새로운 메세지가 발생했다 ===");
                List<Notification> notifications = u.getNotifications();
                if(notifications.isEmpty()){
                    log.debug("생성된 알림이 없음");
                    // 읽었다 -> 생성함
                    String message = messageService.newMessage(roomId,userId);
                    String uri = messageService.getPath(roomId);
                    ChatNotificationRequest req = ChatNotificationRequest.of(message,uri, Set.of(u.getUser().getId()),u.getId(),null, NotificationType.CHAT);
                    notificationSender.send(req);
                }
                for(Notification noty : notifications){
                    log.debug("=== notification 도는중 === ");
                    if(noty.isRead()){
                        // 읽었다 -> 생성함
                        String message = messageService.newMessage(roomId,userId);
                        String uri = messageService.getPath(roomId);
                        ChatNotificationRequest req = ChatNotificationRequest.of(message,uri,Set.of(u.getUser().getId()),u.getId(),null,NotificationType.CHAT);
                        notificationSender.send(req);
                    }else{
                        // 읽지 않았다 -> 생성 안함
                    }
                }
            }
        }

    }
}
