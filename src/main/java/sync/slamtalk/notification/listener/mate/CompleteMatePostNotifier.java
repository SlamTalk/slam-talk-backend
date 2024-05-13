package sync.slamtalk.notification.listener.mate;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import sync.slamtalk.common.Site;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.event.CompleteMateEvent;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.NotificationRequest;
import sync.slamtalk.notification.model.NotificationType;
import sync.slamtalk.notification.util.StringSlicer;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CompleteMatePostNotifier {

    private final NotificationSender notificationSender;

    private static final String COMPLETE_MATEPOST_MESSAGE =  "’'%s'의 모집이 완료되었습니다.’ '%s'";

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(CompleteMateEvent.class)
    public void completeMatePost(CompleteMateEvent event) {

        MatePost matePost = event.matePost();

        // 알림 요청 객체를 생성합니다.
        NotificationRequest request = NotificationRequest.of(
                String.format(COMPLETE_MATEPOST_MESSAGE, StringSlicer.slice(matePost.getTitle()), LocalDate.now()),
                Site.mateMatching(matePost.getMatePostId()),
                event.userIds(),
                matePost.getWriterId(),
                NotificationType.MATE
        );

        // 알림을 전송합니다.
        notificationSender.send(request);
    }
}
