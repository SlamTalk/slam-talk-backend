package sync.slamtalk.notification.listener.mate;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import sync.slamtalk.common.Site;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.event.MateSupportEvent;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.NotificationRequest;
import sync.slamtalk.notification.model.NotificationType;
import sync.slamtalk.notification.util.StringSlicer;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MatePostSupportNotifier {

    private final NotificationSender notificationSender;

    private static final String APPLICATION_MESSAGE =  "’'%s' 님이 메이트 찾기 '%s' 모집에 지원했습니다.’ '%s'";

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(MateSupportEvent.class)
    public void mateSupport(MateSupportEvent event) {

        MatePost matePost = event.matePost();

        // 알림 요청 객체를 생성합니다.
        NotificationRequest request = NotificationRequest.of(
                String.format(APPLICATION_MESSAGE, event.participantNickname(), StringSlicer.slice(matePost.getTitle()),LocalDate.now()),
                Site.mateMatching(matePost.getMatePostId()),
                Set.of(event.matePost().getWriterId()),
                matePost.getWriterId(),
                NotificationType.MATE
        );

        // 알림을 전송합니다.
        notificationSender.send(request);
    }
}
