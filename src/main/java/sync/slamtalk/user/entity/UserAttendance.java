package sync.slamtalk.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.common.BaseEntity;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "attendance")
@NoArgsConstructor
public class UserAttendance extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "att_date", nullable = false)
    private LocalDate attDate;

    public UserAttendance(User user, LocalDate attDate) {
        this.user = user;
        this.attDate = attDate;
        user.getUserAttendances().add(this); // 연관 관계 매핑
    }
}