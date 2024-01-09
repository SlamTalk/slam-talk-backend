package sync.slamtalk.common;

import jakarta.persistence.Column;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

public class BaseEntity {

    // @CreatedData : entity 생성되어 저장될 때 시간이 자동으로 저장
    @CreatedDate
    @Column(updatable = false,name = "created_at")
    private LocalDateTime createdAt;

    // @LastModifiedDate : 조회환 entity 값을 변경할 때 시간이 자동으로 저장
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}