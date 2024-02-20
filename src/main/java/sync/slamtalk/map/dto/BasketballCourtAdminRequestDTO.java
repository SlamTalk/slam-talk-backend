package sync.slamtalk.map.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketballCourtAdminRequestDTO {
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
    private Long informerId;
    private Long chatroomId;
}
