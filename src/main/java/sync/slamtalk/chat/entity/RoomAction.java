package sync.slamtalk.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoomAction {

    ENTRANCE("ENTRANCE"),
    EXIT("EXIT"),
    BACK("BACK");

    private final String action;
}
