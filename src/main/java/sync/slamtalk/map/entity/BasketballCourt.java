package sync.slamtalk.map.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.common.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "BasketballCourt")
public class BasketballCourt extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "court_id", nullable = false)
    private Long courtId; // 농구장 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatroom; // 채팅방 ID

    @Column(name = "court_name", nullable = false, length = 50)
    private String courtName; // 농구장 이름

    @Column(name = "latitude", precision = 13, scale = 10, nullable = false)
    private BigDecimal latitude; // 농구장 위도

    @Column(name = "longitude", precision = 13, scale = 10, nullable = false)
    private BigDecimal longitude; //농구장 경도

    @Column(name = "court_type", length = 50)
    private String courtType; //코트 종류

    @Column(name = "indoor_outdoor", length = 20)
    private String indoorOutdoor; //실내외

    @Column(name = "court_size", length = 50)
    private String courtSize; // 코트 사이즈

    @Column(name = "hoop_count")
    private Integer hoopCount; // 골대 수

    @Column(name = "night_lighting")
    private Boolean nightLighting; // 야간 조명 유무

    @Column(name = "opening_hours", length = 50)
    private String openingHours; // 개방 시간

    @Column(name = "fee", length = 100)
    private String fee; // 사용료

    @Column(name = "parking_available")
    private Boolean parkingAvailable; // 주차 가능 여부

    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo; //기타 내용

    @Column(name = "photo_url", length = 255)
    private String photoUrl; // 농구장 사진 url

    @Column(name = "admin_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminStatus adminStatus; // 관리자 상태

}
