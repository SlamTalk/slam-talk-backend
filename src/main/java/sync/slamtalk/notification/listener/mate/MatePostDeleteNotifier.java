package sync.slamtalk.notification.listener.mate;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import sync.slamtalk.common.Site;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.event.MatePostPostDeletionEvent;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.NotificationRequest;
import sync.slamtalk.notification.model.NotificationType;
import sync.slamtalk.notification.util.StringSlicer;

@Component
@RequiredArgsConstructor
public class MatePostDeleteNotifier {
    private final NotificationSender notificationSender;

    private static final String MATE_POST_DELETION_REJECTION_MESSAGE =  "메이트 찾기 %s의 모집이 취소되었습니다.";


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(MatePostPostDeletionEvent.class)
    public void acceptMateSupport(MatePostPostDeletionEvent event) {
        MatePost matePost = event.matePost();

        // 알림 요청 객체를 생성합니다.
        NotificationRequest request = NotificationRequest.of(
                String.format(MATE_POST_DELETION_REJECTION_MESSAGE, StringSlicer.slice(matePost.getTitle())),
                Site.mateMatching(matePost.getMatePostId()),
                event.participantUserIds(),
                matePost.getWriterId(),
                NotificationType.MATE
        );

        // 알림을 전송합니다.
        notificationSender.send(request);
    }
}
