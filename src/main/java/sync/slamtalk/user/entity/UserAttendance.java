package sync.slamtalk.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "attendance")
@NoArgsConstructor
public class UserAttendance {
    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "att_date",nullable = false)
    private LocalDate attDate;

    public UserAttendance(User user, LocalDate attDate) {
        this.user = user;
        this.attDate = attDate;
    }

    /* 연관 관계 편의 메서드 */
    public void addUser(User user){
        this.user = user;
        user.getUserAttendances().add(this);
    }

}