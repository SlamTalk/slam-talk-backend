package sync.slamtalk.community.mapper;

import org.springframework.stereotype.Component;
import sync.slamtalk.community.dto.CommentCreateRequestDTO;
import sync.slamtalk.community.dto.CommentResponseDTO;
import sync.slamtalk.community.entity.Comment;
import sync.slamtalk.community.entity.Community;
import sync.slamtalk.user.entity.User;

@Component
public class CommentMapper {

    // CommunityComment 엔티티를 CommunityCommentDto로 변환
    public static CommentResponseDTO toCommentResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .communityId(comment.getCommunity() != null ? comment.getCommunity().getCommunityId() : null)
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .content(comment.getContent())
                .build();
    }

    // CommunityCommentCreateDto와 관련 정보를 사용하여 CommunityComment 엔티티 생성
    public static Comment toEntity(CommentCreateRequestDTO requestDTO, Community community, User user) {
        if (requestDTO == null || community == null || user == null) {
            return null;
        }

        return Comment.builder()
                .community(community)
                .user(user)
                .content(requestDTO.getContent())
                .build();
    }
}
