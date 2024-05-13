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
import sync.slamtalk.team.event.TeamMatchingSupportAcceptanceEvent;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TeamMatchingSupportAcceptanceNotifier {

    private final NotificationSender notificationSender;

    private static final String TEAM_MATCHING_SUPPORT_ACCEPTANCE_MESSAGE =  "‘상대팀 찾기 '%s' 글에 대한 신청이 수락되었습니다.’ '%s'";

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(TeamMatchingSupportAcceptanceEvent.class)
    public void acceptMateSupport(TeamMatchingSupportAcceptanceEvent event) {
        TeamMatching teamMatching = event.teamMatching();

        // 알림 요청 객체를 생성합니다.
        NotificationRequest request = NotificationRequest.of(
                String.format(TEAM_MATCHING_SUPPORT_ACCEPTANCE_MESSAGE, StringSlicer.slice(teamMatching.getTitle()), LocalDate.now()),
                Site.teamMatching(teamMatching.getTeamMatchingId()),
                Set.of(event.applicationUserId()),
                teamMatching.getWriter().getId(),
                NotificationType.TEAM
        );

        // 알림을 전송합니다.
        notificationSender.send(request);
    }
}
