package sync.slamtalk.mate.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TestCondition {
    private String name;
    private String location;
    private String sport;
    private LocalDateTime cursorTime;
}
