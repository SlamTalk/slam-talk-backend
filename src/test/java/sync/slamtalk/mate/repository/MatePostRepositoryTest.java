package sync.slamtalk.mate.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sync.slamtalk.mate.entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@DataJpaTest
class MatePostRepositoryTest {
    @Autowired
    private MatePostRepository matePostRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    @DisplayName("메이트 참여 완료된 사용자 개수 세는 레포")
    @Disabled
    void findMateCompleteParticipationCount() {
        MatePost matePost = new MatePost(
                1L,
                1L, // 추후 유저 엔티티로 변환될 수 있음.
                "locationDetail",
                "title",
                "content",
                RecruitedSkillLevelType.HIGH,
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                RecruitmentStatusType.COMPLETED,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                new ArrayList<>());
        MatePost matepost = matePostRepository.save(matePost);
        Participant participant = new Participant(
                1L,
                matepost,
                1L,
                "nickname",
                ApplyStatusType.ACCEPTED,
                PositionType.CENTER,
                SkillLevelType.HIGH);

        participantRepository.save(participant);
        
        Long mateCompleteParticipationCount = matePostRepository.findMateCompleteParticipationCount(1L);
        Assertions.assertEquals(mateCompleteParticipationCount, 1L);
    }
}