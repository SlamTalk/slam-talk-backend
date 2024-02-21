package sync.slamtalk.map.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketballCourtReportResponseDTO {
    private Long courtId;
    private String courtName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String courtType;
    private String indoorOutdoor;
    private String courtSize;
    private Integer hoopCount;
    private String nightLighting;
    private String openingHours;
    private String fee;
    private String parkingAvailable;
    private String phoneNum;
    private String website;
    private List<String> convenience;
    private String additionalInfo;
    private String photoUrl;
    private Long informerId;
    private String adminStatus;
}
