package sync.slamtalk.community.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.community.dto.CommentCreateRequestDTO;
import sync.slamtalk.community.dto.CommentResponseDTO;
import sync.slamtalk.community.dto.CommunityErrorResponseCode;
import sync.slamtalk.community.entity.Comment;
import sync.slamtalk.community.entity.Community;
import sync.slamtalk.community.mapper.CommentMapper;
import sync.slamtalk.community.repository.CommentRepository;
import sync.slamtalk.community.repository.CommunityRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    // 댓글 입력
    @Transactional
    public CommentResponseDTO createComment(CommentCreateRequestDTO requestDTO, Long userId,
                                            Long communityId) {

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.USER_NOT_FOUND));

        if (requestDTO.getContent().isEmpty()) {
            throw new BaseException(CommunityErrorResponseCode.COMMENT_FAIL);
        }

        Comment comment = CommentMapper.toEntity(requestDTO, community, user);
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentResponseDto(comment);
    }

    // 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getCommentsList(Long communityId) {
        List<Comment> comments = commentRepository.findByCommunity_communityIdAndIsDeletedFalse(communityId);
        return comments.stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    // 댓글 수정
    @Transactional
    public CommentResponseDTO editComment(CommentCreateRequestDTO requestDTO, Long userId, Long commentId,
                                          Long communityId) {
        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.COMMENT_NOT_FOUND));

        if (!comment.getCommunity().getCommunityId().equals(communityId)) {
            throw new BaseException(CommunityErrorResponseCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new BaseException(CommunityErrorResponseCode.UNAUTHORIZED_USER);
        }

        if (requestDTO.getContent() != null && !requestDTO.getContent().isEmpty()) {
            comment.editComment(requestDTO.getContent());
        } else {
            throw new BaseException(CommunityErrorResponseCode.COMMENT_FAIL);
        }

        comment = commentRepository.save(comment);
        return CommentMapper.toCommentResponseDto(comment);
    }

    // 댓글 삭제
    @Transactional
    public CommentResponseDTO deleteComment(Long userId, Long commentId,Long communityId) {
        Comment comment = commentRepository.findByCommentIdAndIsDeletedFalse(commentId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.COMMENT_NOT_FOUND));

        if (!comment.getCommunity().getCommunityId().equals(communityId)) {
            throw new BaseException(CommunityErrorResponseCode.COMMENT_NOT_FOUND);
        }

        if (!comment.getUser().getId().equals(userId)) {
            throw new BaseException(CommunityErrorResponseCode.UNAUTHORIZED_USER);
        }

        comment.delete();
        comment = commentRepository.save(comment);

        return CommentMapper.toCommentResponseDto(comment);
    }
}
