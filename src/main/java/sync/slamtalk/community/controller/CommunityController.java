package sync.slamtalk.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.community.dto.CommunityCreateRequestDTO;
import sync.slamtalk.community.dto.CommunityEditRequestDTO;
import sync.slamtalk.community.dto.CommunityResponseDTO;
import sync.slamtalk.community.entity.CommunityCategory;
import sync.slamtalk.community.service.CommunityService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommunityController {
    private final CommunityService communityService;


    // 게시글 등록
    @PostMapping("/new-form")
    @Operation(
            summary = "게시글 등록",
            description = "이 기능은 이용자가 작성한 게시글 정보를 등록하는 기능입니다.",
            tags = {"게시판"}
    )
    public ApiResponse<CommunityResponseDTO> createCommunity(
            @RequestPart(name = "requestDTO", required = false) CommunityCreateRequestDTO requestDTO,
            @RequestPart(name = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal Long userId) {

        CommunityResponseDTO responseDTO = communityService.createCommunity(requestDTO, images, userId);
        return ApiResponse.ok(responseDTO, "게시글을 성공적으로 등록하였습니다.");
    }

    // 게시글 수정
    @PatchMapping("/edit/{communityId}")
    @Operation(
            summary = "게시글 수정",
            description = "이 기능은 이용자가 작성한 게시글 정보를 수정하는 기능입니다.",
            tags = {"게시판"}
    )
    public ApiResponse<CommunityResponseDTO> editCommunity(@PathVariable Long communityId,
                                                           @RequestPart(name = "requestDTO", required = false) CommunityEditRequestDTO requestDTO,
                                                           @RequestPart(name = "images", required = false) List<MultipartFile> images,
                                                           @AuthenticationPrincipal Long userId) {
        CommunityResponseDTO responseDTO = communityService.editCommunity(communityId, requestDTO, images, userId);
        return ApiResponse.ok(responseDTO, "게시글을 성공적으로 수정했습니다.");
    }

    // 게시글 목록 조회
    @GetMapping("/board")
    @Operation(
            summary = "게시글 목록 조회",
            description = "이 기능은 작성된 게시글 목록 정보를 조회하는 기능입니다.",
            tags = {"게시판","게스트"}
    )
    public ApiResponse<List<CommunityResponseDTO>> getPostList() {
        List<CommunityResponseDTO> communities = communityService.getPostList();
        return ApiResponse.ok(communities);
    }

    // 게시글 조회
    @GetMapping("/board/{communityId}")
    @Operation(
            summary = "게시글 조회",
            description = "이 기능은 작성된 게시글 정보를 조회하는 기능입니다.",
            tags = {"게시판","게스트"}
    )
    public ApiResponse<CommunityResponseDTO> getPost(@PathVariable Long communityId) {
        CommunityResponseDTO communityResponseDTO = communityService.getPost(communityId);
        return ApiResponse.ok(communityResponseDTO);
    }

    //태그 별 게시글 목록 조회
    @GetMapping("/category/{category}")
    @Operation(
            summary = "태그 별 게시글 목록 조회",
            description = "이 기능은 태그 별로 작성된 게시글 목록 정보를 조회하는 기능입니다.",
            tags = {"게시판","게스트"}
    )
    public ApiResponse<List<CommunityResponseDTO>> getCommunitiesByCategory(
            @PathVariable("category") CommunityCategory category) {
        List<CommunityResponseDTO> communities = communityService.getPostListByCategory(category);
        return ApiResponse.ok(communities);
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{communityId}")
    @Operation(
            summary = "게시글 삭제",
            description = "이 기능은 작성된 게시글을 삭제하는 기능입니다.",
            tags = {"게시판"}
    )
    public ApiResponse<String> deleteCommunity(@PathVariable Long communityId, @AuthenticationPrincipal Long userId) {
        communityService.deleteCommunity(communityId, userId);
        return ApiResponse.ok("게시글을 성공적으로 삭제하였습니다.");
    }
}
