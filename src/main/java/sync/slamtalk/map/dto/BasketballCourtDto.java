package sync.slamtalk.map.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이 있는 필드는 제외
public class BasketballCourtDto {
    private Long courtId;
    private String courtName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String courtType;
    private String indoorOutdoor;
    private String courtSize;
    private Integer hoopCount;
    private Boolean nightLighting;
    private Boolean openingHours;
    private Boolean fee;
    private Boolean parkingAvailable;
    private String phoneNum;
    private String website;
    private String convenience;
    private String additionalInfo;
    private String photoUrl;

    //농구장 전체 정보 dto
    public BasketballCourtDto(Long courtId, String courtName, String address, BigDecimal latitude, BigDecimal longitude,
                              String courtType, String indoorOutdoor, String courtSize, Integer hoopCount,
                              Boolean nightLighting, Boolean openingHours, Boolean fee, Boolean parkingAvailable,
                              String phoneNum, String website,
                              String convenience,
                              String additionalInfo, String photoUrl) {

        this.courtId = courtId;
        this.courtName = courtName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.courtType = courtType;
        this.indoorOutdoor = indoorOutdoor;
        this.courtSize = courtSize;
        this.hoopCount = hoopCount;
        this.nightLighting = nightLighting;
        this.openingHours = openingHours;
        this.fee = fee;
        this.parkingAvailable = parkingAvailable;
        this.phoneNum = phoneNum;
        this.website = website;
        this.convenience = convenience;
        this.additionalInfo = additionalInfo;
        this.photoUrl = photoUrl;
    }

    // 농구장 간략 정보 dto
    public BasketballCourtDto(Long courtId, String courtName, String address, BigDecimal latitude, BigDecimal longitude) {
        this.courtId = courtId;
        this.courtName = courtName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
