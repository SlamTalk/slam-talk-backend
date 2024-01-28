package sync.slamtalk.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreRemove;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@EnableJpaAuditing
@Configuration
public class BaseEntity {

    // @CreatedData : entity 생성되어 저장될 때 시간이 자동으로 저장
    @CreatedDate
    @Column(updatable = false,name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    // @LastModifiedDate : 조회환 entity 값을 변경할 때 시간이 자동으로 저장
    @LastModifiedDate
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted")
    private Boolean isDeleted = false;


    // @PreRemove를 사용하여 엔티티가 삭제될 때 isDeleted를 true로 설정
    @PreRemove
    public void delete() {
        this.isDeleted = true;
    }

}