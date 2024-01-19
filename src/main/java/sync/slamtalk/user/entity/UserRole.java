package sync.slamtalk.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * User 역할 클래스
 * Guest, User, Admin 으로 구성
 * */
@Getter
@RequiredArgsConstructor
public enum UserRole {

    GEUST("ROLE_GEUST"), USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

    private final String key;
}
