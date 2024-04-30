package sync.slamtalk.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자
 */
@Getter
@RequiredArgsConstructor
public enum UserBasketballSkillLevelType {
    HIGH("고수"), MIDDLE("중수"), LOW("하수"), BEGINNER("입문");

    private final String level;
}
