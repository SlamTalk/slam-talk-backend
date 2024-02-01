package sync.slamtalk.team.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sync.slamtalk.mate.entity.ApplyStatusType;
import sync.slamtalk.team.entity.TeamMatching;

@Getter
@Setter
public class ToApplicantDto {

    long teamApplicantTableId;
    long applicantId;
    long chatroomId;
    long TeamMatchingId;
    ApplyStatusType applyStatusType;
    String applicantNickname;
}
