package sync.slamtalk.community.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.community.entity.CommunityCategory;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이 있는 필드는 제외
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityResponseDTO {
    private Long communityId;
    private String title;
    private String content;
    private String userNickname;
    private Long userId;
    private CommunityCategory category;
    private List<String> imageUrls;
    private LocalDateTime updatedAt;
    private List<CommentResponseDTO> comments;
    private Long commentCount;
}
