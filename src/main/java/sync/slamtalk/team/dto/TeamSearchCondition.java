package sync.slamtalk.team.dto;

import lombok.Getter;
import sync.slamtalk.mate.entity.SkillLevelType;

import java.time.LocalDateTime;


@Getter
public class TeamSearchCondition {
    private SkillLevelType skillLevel;
    private String location;
    private String nov; // number of Versus (ex."3"(3:3), "5"(5:5))
    private LocalDateTime cursorTime;

    // 검색어 (모집 글 제목을 기준으로 필터링 할 용도)
    private String searchWord;

    private boolean includingExpired; // 만료된 팀 매칭도 포함할지 여부.
}
