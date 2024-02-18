package sync.slamtalk.map.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값이 있는 필드는 제외
public class BasketballCourtReportSummaryDTO {
    private Long courtId;
    private String courtName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String adminStatus;

    public BasketballCourtReportSummaryDTO(Long courtId, String courtName, String address, BigDecimal latitude, BigDecimal longitude, String adminStatus ) {
        this.courtId = courtId;
        this.courtName = courtName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adminStatus = adminStatus;
    }

}
