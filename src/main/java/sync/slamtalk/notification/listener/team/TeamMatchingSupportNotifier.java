package sync.slamtalk.notification.listener.team;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import sync.slamtalk.common.Site;
import sync.slamtalk.notification.NotificationSender;
import sync.slamtalk.notification.dto.request.NotificationRequest;
import sync.slamtalk.notification.model.NotificationType;
import sync.slamtalk.notification.util.StringSlicer;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.team.event.TeamMatchingSupportEvent;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TeamMatchingSupportNotifier {

    private final NotificationSender notificationSender;

    private static final String APPLICATION_MESSAGE =  "%s님이 상대팀 찾기 %s 모집에 지원했습니다.";

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(TeamMatchingSupportEvent.class)
    public void mateSupport(TeamMatchingSupportEvent event) {

        TeamMatching teamMatching = event.teamMatching();

        // 알림 요청 객체를 생성합니다.
        NotificationRequest request = NotificationRequest.of(
                String.format(APPLICATION_MESSAGE, event.participantNickname(), StringSlicer.slice(teamMatching.getTitle())),
                Site.teamMatching(teamMatching.getTeamMatchingId()),
                Set.of(event.writerUserId()),
                teamMatching.getWriter().getId(),
                NotificationType.TEAM
        );

        // 알림을 전송합니다.
        notificationSender.send(request);
    }
}
