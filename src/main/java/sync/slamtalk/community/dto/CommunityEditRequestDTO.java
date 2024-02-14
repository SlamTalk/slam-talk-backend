package sync.slamtalk.community.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.community.entity.CommunityCategory;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityEditRequestDTO {
    private String title;
    private String content;
    private CommunityCategory category;
    private List<String> imageUrls;
}
