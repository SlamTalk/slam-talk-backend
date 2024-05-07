package sync.slamtalk.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sync.slamtalk.common.ApiResponse;
import sync.slamtalk.notification.dto.response.NotificationResponse;
import sync.slamtalk.notification.service.NotificationService;

import java.util.List;

/**
 * Notification 관련 API를 제공하는 컨트롤러 클래스입니다.
 */
@Tag(name = "Notification API", description = "알림 관련 API를 제공합니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 사용자의 알림 목록을 조회합니다.
     * 별도의 페이지네이션 없이 제공합니다.
     *
     * @param userId 로그인한 사용자의 정보
     * @return 알림 목록
     */
    @Operation(summary = "알림 조회", description = "로그인한 사용자의 알림을 조회합니다.")
    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.ok(notificationService.getNotificationsByMemberId(userId));
    }

    /**
     * 사용자의 알림을 일괄적으로 읽음 처리합니다.
     *
     * @param userId 로그인한 사용자의 정보
     * @return 성공 여부
     */
    @Operation(summary = "알림 일괄 읽음 처리", description = "사용자의 알림 전체를 읽음 처리 합니다.")
    @PatchMapping
    public ApiResponse<Void> readAllNotifications(
            @AuthenticationPrincipal Long userId
    ) {
        notificationService.readAllNotifications(userId);
        return ApiResponse.ok();
    }

    /**
     * 사용자의 알림을 개별적으로 읽음 처리합니다.
     *
     * @param userId 로그인한 사용자의 정보
     * @return 성공 여부
     */
    @Operation(summary = "알림 개별 읽음 처리", description = "사용자의 알림 하나를 읽음 처리합니다.")
    @PatchMapping("/{notificationId}")
    public ApiResponse<Void> readNotifications(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Long userId
    ) {
        notificationService.readNotification(notificationId, userId);
        return ApiResponse.ok();
    }

    /**
     * 사용자의 알림을 개별 삭제합니다.
     *
     * @param userId 로그인한 사용자의 정보
     * @return 성공 여부
     */
    @Operation(summary = "알림 일괄 삭제", description = "사용자의 알림 전체를 삭제합니다.")
    @DeleteMapping
    public ApiResponse<Void> deleteAllNotifications(
            @AuthenticationPrincipal Long userId
    ) {
        notificationService.deleteAllNotifications(userId);
        return ApiResponse.ok();
    }

    /**
     * 사용자의 알림을 일괄 삭제합니다.
     *
     * @param userId 로그인한 사용자의 정보
     * @return 성공 여부
     */
    @Operation(summary = "알림 개별 삭제", description = "사용자의 알림 하나를 삭제합니다.")
    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotifications(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal Long userId
    ) {
        notificationService.deleteNotification(notificationId, userId);
        return ApiResponse.ok();
    }

}
