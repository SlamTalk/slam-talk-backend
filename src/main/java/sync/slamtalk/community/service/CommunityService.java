package sync.slamtalk.community.service;

import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.common.s3bucket.repository.AwsS3RepositoryImpl;
import sync.slamtalk.community.dto.CommentResponseDTO;
import sync.slamtalk.community.dto.CommunityCreateRequestDTO;
import sync.slamtalk.community.dto.CommunityEditRequestDTO;
import sync.slamtalk.community.dto.CommunityErrorResponseCode;
import sync.slamtalk.community.dto.CommunityResponseDTO;
import sync.slamtalk.community.entity.Community;
import sync.slamtalk.community.entity.CommunityCategory;
import sync.slamtalk.community.entity.CommunityImage;
import sync.slamtalk.community.mapper.CommunityMapper;
import sync.slamtalk.community.repository.CommentRepository;
import sync.slamtalk.community.repository.CommunityRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.User;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {
    private final CommentRepository commentRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMapper communityMapper;
    private final UserRepository userRepository;
    private final AwsS3RepositoryImpl awsS3Repository;
    private final CommentService commentService;

    // 게시글 등록
    @Transactional
    public CommunityResponseDTO createCommunity(CommunityCreateRequestDTO requestDTO, List<MultipartFile> images,
                                                Long userId) {

        validateCreateCommunityRequest(requestDTO); // 엽력 값 검사

        // 사용자 조회 및 예외 처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.USER_NOT_FOUND));

        // 이미지 리스트 null 체크 및 빈 리스트 처리 추가
        images = (images == null) ? new ArrayList<>() : images;

        try {
            // DTO -> entity, 이미지 업로드 처리
            Community community = communityMapper.toCommunityEntity(requestDTO, user);
            List<CommunityImage> communityImages = validateAndUploadImages(images, community); // 다중 이미지 업로드
            if (communityImages != null) { // 이미지 리스트가 null이 아닐 때만, 업데이트
                community.updateImages(communityImages);
            }

            // 게시글 저장 및 DTO 반환
            Community savedCommunity = communityRepository.save(community);
            return communityMapper.toCommunityResponseDTO(savedCommunity);
        } catch (Exception e) {
            log.error("게시글 등록 실패 : {}", e.getMessage());
            throw new BaseException(CommunityErrorResponseCode.POST_FAIL);
        }

    }

    // 게시글 수정
    @Transactional
    public CommunityResponseDTO editCommunity(Long communityId, CommunityEditRequestDTO requestDTO,
                                              List<MultipartFile> images, Long userId) {

        // 게시글 조회 및 예외 처리
        Community community = communityRepository.findByCommunityIdAndIsDeletedFalse(communityId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.POST_NOT_FOUND));

        // 사용자 조회 및 예외 처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.USER_NOT_FOUND));

        // 게시글을 수정하려는 사용자가 게시글의 작성자와 동일한지 확인 = 수정 권한 검증
        if (!community.getUser().getId().equals(userId)) {
            log.error("Unauthorized user attempt to edit post. Authorized User ID:{}, Attempt User ID:{}",
                    community.getUser().getId(), userId);
            throw new BaseException(CommunityErrorResponseCode.UNAUTHORIZED_USER);
        }

        // 제목 수정
        if (!isNull(requestDTO.getTitle()) && !isEmpty(requestDTO.getTitle())) {
            community.editTitle(requestDTO.getTitle());
        }
        // 본문 수정
        if (!isNull(requestDTO.getContent()) && !isEmpty(requestDTO.getContent())) {
            community.editContent(requestDTO.getContent());
        }
        // 카테고리 수정
        if (requestDTO.getCategory() != null) {
            community.editCategory(requestDTO.getCategory());
        }

        // 이미지 검증, 업로드 및 수정
        List<CommunityImage> communityImages = validateAndUploadImages(images, community); // 다중 이미지 업로드
        if (communityImages != null) {
            community.editImages(communityImages);
        }

        // 수정된 게시글 저장 및 DTO 반환
        Community editCommunity = communityRepository.save(community);
        return communityMapper.toCommunityResponseDTO(editCommunity);
    }

    // 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<CommunityResponseDTO> getPostList() {
        List<Community> communities = communityRepository.findByIsDeletedFalse();
        Map<Community, Long> commentCounts = new HashMap<>();

        for (Community community : communities) {
            commentCounts.put(community, commentRepository.countByCommunityAndIsDeletedFalse(community));
        }
        return communityMapper.toCommunityResponseDTOList(communities, commentCounts);
    }

    // 태그 별 게시글 목록 조회
    @Transactional(readOnly = true)
    public List<CommunityResponseDTO> getPostListByCategory(CommunityCategory category) {
        List<Community> communities = communityRepository.findByCategoryAndIsDeletedFalse(category);
        Map<Community, Long> commentCounts = new HashMap<>();

        for (Community community : communities) {
            commentCounts.put(community, commentRepository.countByCommunityAndIsDeletedFalse(community));
        }
        return communityMapper.toCommunityResponseDTOList(communities, commentCounts);
    }

    //단일 게시글 조회
    @Transactional(readOnly = true)
    public CommunityResponseDTO getPost(Long communityId) {
        Community community = communityRepository.findByCommunityIdAndIsDeletedFalse(communityId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.POST_NOT_FOUND));

        List<CommentResponseDTO> commentResponseDTO = commentService.getCommentsList(communityId);

        return communityMapper.toCommunityAndCommentResponseDTO(community,commentResponseDTO);
    }

    // 게시글 삭제
    @Transactional
    public void deleteCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.POST_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(CommunityErrorResponseCode.USER_NOT_FOUND));

        if (!community.getUser().getId().equals(userId)) {
            log.error("Unauthorized user attempt to delete post. Authorized User ID:{}, Attempt User ID:{}",
                    community.getUser().getId(), userId);
            throw new BaseException(CommunityErrorResponseCode.UNAUTHORIZED_USER);
        }

        community.delete();
        communityRepository.save(community);

    }

    // 이미지 유효성 검사 및 업로드 -> 사용자가 업로드한 이미지가 있는지 확인 후, 업로드
    private List<CommunityImage> validateAndUploadImages(List<MultipartFile> images,Community community) {
        if (images == null || images.isEmpty()) {
            return Collections.emptyList(); // 이미지가 없는 경우 빈 리스트 반환
        }

        // 이미지가 비어있는지 검사 후 업로드
        // images 리스트에 실제로 내용이 있는 파일만 필터링 후 업로드
        List<String> imageUrls = awsS3Repository.uploadFiles(images.stream()
                .filter(image -> !image.isEmpty())
                .toList());

        return imageUrls.stream()
                .map(url -> new CommunityImage(null, community, url))
                .toList();
    }

    // 게시글 등록 입력 데이터(제목,내용,태그) 검증
    private void validateCreateCommunityRequest(CommunityCreateRequestDTO requestDTO) {
        if ((isNull(requestDTO.getTitle()) || isEmpty(requestDTO.getTitle())) || (isNull(requestDTO.getContent()) || isEmpty(requestDTO.getContent())) || requestDTO.getCategory() == null) {
            throw new BaseException(CommunityErrorResponseCode.POST_FAIL);
        }
    }

    private boolean isNull(String field) {
        return field == null; // 입력 값 Null 검증
    }

    private boolean isEmpty(String field) {
        return field.trim().isEmpty(); // 입력 값 공백 검증
    }
}
