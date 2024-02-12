package sync.slamtalk.community.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.community.entity.CommunityCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCreateRequestDTO {
    private String title;
    private String content;
    private CommunityCategory category;
}
