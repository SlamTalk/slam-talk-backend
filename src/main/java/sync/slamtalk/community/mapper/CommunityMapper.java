package sync.slamtalk.community.mapper;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import sync.slamtalk.community.dto.CommunityCreateRequestDTO;
import sync.slamtalk.community.dto.CommunityResponseDTO;
import sync.slamtalk.community.entity.Community;
import sync.slamtalk.community.entity.CommunityImage;
import sync.slamtalk.user.entity.User;

@Component
public class CommunityMapper {

    public Community toCommunityEntity(CommunityCreateRequestDTO requestDTO, User user) {
        // 빌더 패턴을 사용하여 Community 인스턴스 생성
        Community community = Community.builder()
                .user(user)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .category(requestDTO.getCategory())
                .build();

        return community;
    }

    public CommunityResponseDTO toCommunityResponseDTO(Community community) {
        List<String> imageUrls = Optional.ofNullable(community.getImages())
                .orElse(Collections.emptyList()) // images가 null일 경우 빈 리스트 사용
                .stream()
                .map(CommunityImage::getImageUrl)
                .filter(url -> url != null && !url.trim().isEmpty()) // 공백과 null이 아닌 URL만 필터링
                .collect(Collectors.toList());


        return CommunityResponseDTO.builder()
                .communityId(community.getCommunityId())
                .title(community.getTitle())
                .userNickname(community.getUser().getNickname())
                .userId(community.getUser().getId())
                .content(community.getContent())
                .category(community.getCategory())
                .imageUrls(imageUrls)
                .updatedAt(community.getUpdatedAt())
                .build();
    }

    // Community 엔티티 리스트를 CommunityResponseDTO 리스트로 변환
    public List<CommunityResponseDTO> toCommunityResponseDTOList(List<Community> communities) {
        return communities.stream()
                .map(community -> CommunityResponseDTO.builder()
                        .communityId(community.getCommunityId())
                        .title(community.getTitle())
                        .category(community.getCategory())
                        .userNickname(community.getUser().getNickname())
                        .userId(community.getUser().getId())
                        .build())
                .collect(Collectors.toList());
    }
}
