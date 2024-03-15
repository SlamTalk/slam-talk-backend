package sync.slamtalk.mate.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import sync.slamtalk.mate.dto.PositionListDto;
import sync.slamtalk.mate.entity.RecruitedSkillLevelType;
import sync.slamtalk.mate.entity.RecruitmentStatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Objective : 메이트찾기 목록 조회(필터링 기능을 갖춘)를 위한 서버에서 클라이언트로 보내는 Dto
 */

@Data
public class MatePostToDto {

    private long matePostId;
    private long writerId;
    private String writerNickname;
    private String writerImageUrl;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduledDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private String title;
    private String content;
    private RecruitmentStatusType recruitmentStatus;
    private String locationDetail;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    private List<PositionListDto> positionList = new ArrayList<>();
    private RecruitedSkillLevelType skillLevel;
    private List<String> skillLevelList = new ArrayList<>();
    private List<ParticipantDto> participants = new ArrayList<>();

}
