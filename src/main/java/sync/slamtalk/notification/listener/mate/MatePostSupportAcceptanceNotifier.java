package sync.slamtalk.notification.listener.mate;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import sync.slamtalk.common.Site;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.event.MateSupportAcceptanceEvent;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.NotificationRequest;
import sync.slamtalk.notification.model.NotificationType;
import sync.slamtalk.notification.util.StringSlicer;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MatePostSupportAcceptanceNotifier {
    private final NotificationSender notificationSender;

    private static final String MATE_SUPPORT_ACCEPTANCE_MESSAGE =  "‘메이트 찾기 '%s' 글에 대한 신청이 수락되었습니다.’ '%s'";

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(MateSupportAcceptanceEvent.class)
    public void acceptMateSupport(MateSupportAcceptanceEvent event) {
        MatePost matePost = event.matePost();

        // 알림 요청 객체를 생성합니다.
        NotificationRequest request = NotificationRequest.of(
                String.format(MATE_SUPPORT_ACCEPTANCE_MESSAGE, StringSlicer.slice(matePost.getTitle()), LocalDate.now()),
                Site.mateMatching(matePost.getMatePostId()),
                Set.of(event.applicationUserId()),
                matePost.getWriterId(),
                NotificationType.MATE
        );

        // 알림을 전송합니다.
        notificationSender.send(request);
    }
}
