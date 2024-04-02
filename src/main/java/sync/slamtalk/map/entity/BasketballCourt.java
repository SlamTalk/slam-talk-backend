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
    private Long id; // 농구장 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatroom; // 채팅방 ID

    @Column(name = "court_name", nullable = false, length = 50)
    private String courtName; // 농구장 이름

    @Column(nullable = false, length = 50)
    private String address; // 농구장 주소

    @Column(precision = 13, scale = 10, nullable = false)
    private BigDecimal latitude; // 농구장 위도

    @Column(precision = 13, scale = 10, nullable = false)
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
    @Enumerated(EnumType.STRING)
    private NightLighting nightLighting; // 야간 조명 유무

    @Column(name = "opening_hours", length = 50)
    @Enumerated(EnumType.STRING)
    private OpeningHours openingHours; // 개방 시간

    @Enumerated(EnumType.STRING)
    private Fee fee; // 사용료

    @Column(name = "parking_available")
    @Enumerated(EnumType.STRING)
    private ParkingAvailable parkingAvailable; // 주차 가능 여부

    @Column(name = "phone_num", length = 20)
    private String phoneNum; // 전화번호

    private String website; // 홈페이지 링크

    private String convenience; // 편의 시설

    @Column(name = "additional_info", columnDefinition = "TEXT")
    private String additionalInfo; //기타 내용

    @Column(name = "photo_url", length = 2048)
    private String photoUrl; // 농구장 사진 url

    @Column(name = "admin_status")
    @Enumerated(EnumType.STRING)
    private AdminStatus adminStatus; // 관리자 상태

    @Column(name = "informer_id")
    private Long informerId; // 제보자 ID


    // 농구장 이름 업데이트
    // 농구장

    //코트 타입 업데이트
    public void updateCourtType(String courtType) {
        this.courtType = courtType;
    }

    //실내외 정보 업데이트
    public void updateIndoorOutDoor(String indoorOutdoor) {
        this.indoorOutdoor = indoorOutdoor;
    }
    //코드 크기 업데이트
    public void updateCourtSize(String courtSize) {
        this.courtSize = courtSize;
    }
    //골대 개수 업데이트
    public void updateHoopCount(Integer hoopCount) {
        this.hoopCount = hoopCount;
    }
    //야간 조명 유무 업데이트
    public void updateNightLighting(NightLighting nightLighting) {
        this.nightLighting = nightLighting;
    }
    //영업시간 업데이트
    public void updateOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }
    //입장료 업데이트
    public void updateFee(Fee fee) {
        this.fee = fee;
    }
    //주차 가능 유무 업데이트
    public void updateParkingAvailable(ParkingAvailable parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }
    //전화번호 업데이트
    public void updatePhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    //웹 사이트 url 업데이트
    public void updateWebsite(String website) {
        this.website = website;
    }
    //편의 시설 업데이트
    public void updateConvenience(String convenience) {
        this.convenience = convenience;
    }
    //기타 정보 업데이트
    public void updateAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
    //농구장 사진 업데이트
    public void updatePhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    // AdminStatus 변경 메소드
    public void updateAdminStatus(AdminStatus adminStatus) {
        this.adminStatus = adminStatus;
    }

    // 채팅방 업데이트
    public void updateChatroom(ChatRoom chatroom) {
        this.chatroom = chatroom;
    }

}
