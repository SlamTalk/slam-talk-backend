package sync.slamtalk.notification.listener.mate;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import sync.slamtalk.common.Site;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.event.MateDeclineEvent;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.NotificationRequest;
import sync.slamtalk.notification.model.NotificationType;
import sync.slamtalk.notification.util.StringSlicer;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class MatePostRejectSupportNotifier {
    private final NotificationSender notificationSender;

    private static final String MATE_SUPPORT_REJECTION_MESSAGE =  "메이트 찾기 %s 글에 대한 신청이 거절되었습니다.";


    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(MateDeclineEvent.class)
    public void acceptMateSupport(MateDeclineEvent event) {
        MatePost matePost = event.matePost();

        // 알림 요청 객체를 생성합니다.
        NotificationRequest request = NotificationRequest.of(
                String.format(MATE_SUPPORT_REJECTION_MESSAGE, StringSlicer.slice(matePost.getTitle())),
                Site.mateMatching(matePost.getMatePostId()),
                Set.of(event.applicationUserId()),
                matePost.getWriterId(),
                NotificationType.MATE
        );

        // 알림을 전송합니다.
        notificationSender.send(request);
    }
}
