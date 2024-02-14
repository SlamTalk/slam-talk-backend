package sync.slamtalk.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.community.dto.CommentCreateRequestDTO;
import sync.slamtalk.community.dto.CommentResponseDTO;
import sync.slamtalk.community.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/{communityId}")
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping("/new-comment")
    @Operation(
            summary = "댓글 등록",
            description = "이 기능은 댓글을 등록하는 기능입니다.",
            tags = {"게시판"}
    )
    public ApiResponse<CommentResponseDTO> addComment(@PathVariable Long communityId,
                                                      @RequestBody CommentCreateRequestDTO requestDTO,
                                                      @AuthenticationPrincipal Long userId) {

        CommentResponseDTO responseDTO = commentService.createComment(requestDTO, userId, communityId);
        return ApiResponse.ok(responseDTO, "댓글을 성공적으로 등록하였습니다.");
    }

    // 댓글 조회
    @GetMapping("/comment")
    @Operation(
            summary = "댓글 조회",
            description = "이 기능은 작성된 댓글을 조회하는 기능입니다.",
            tags = {"게시판"}
    )
    public ApiResponse<List<CommentResponseDTO>> getComment(@PathVariable Long communityId) {
        List<CommentResponseDTO> comments = commentService.getCommentsList(communityId);
        return ApiResponse.ok(comments);

    }

    // 댓글 수정
    @PatchMapping("/edit/{commentId}")
    @Operation(
            summary = "댓글 수정",
            description = "이 기능은 작성된 댓글을 수정하는 기능입니다.",
            tags = {"게시판"}
    )
    public ApiResponse<CommentResponseDTO> editComment(@PathVariable Long commentId,
                                                       @PathVariable Long communityId,
                                                       @RequestBody CommentCreateRequestDTO requestDTO,
                                                       @AuthenticationPrincipal Long userId) {
        CommentResponseDTO responseDTO = commentService.editComment(requestDTO, userId, commentId,communityId);
        return ApiResponse.ok(responseDTO, "댓글을 성공적으로 수정하였습니다.");
    }

    // 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            description = "이 기능은 작성된 댓글을 삭제하는 기능입니다.",
            tags = {"게시판"}
    )
    public ApiResponse<CommentResponseDTO> deleteComment(@PathVariable Long commentId,
                                                         @PathVariable Long communityId,
                                                         @AuthenticationPrincipal Long userId) {
        CommentResponseDTO responseDTO = commentService.deleteComment(userId, commentId,communityId);
        return ApiResponse.ok(responseDTO, "댓글을 성공적으로 삭제하였습니다.");
    }
}
