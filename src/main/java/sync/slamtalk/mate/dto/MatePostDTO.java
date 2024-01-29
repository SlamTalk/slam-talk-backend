package sync.slamtalk.mate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import sync.slamtalk.mate.entity.Participant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MatePostDTO {
    @NonNull
    private long matePostId;
    @NonNull
    private long writerId;git
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startScheduledTime;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endScheduledTime;
    @NonNull
    private String title;
    @NonNull
    private String content;
    @NonNull
    private List<PositionListDTO> positionList = new ArrayList<>();
    @NonNull
    private List<String> skillList = new ArrayList<>();
    @NonNull
    private String locationDetail;
    @NonNull
    private List<Participant> participants = new ArrayList<>();
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
}
