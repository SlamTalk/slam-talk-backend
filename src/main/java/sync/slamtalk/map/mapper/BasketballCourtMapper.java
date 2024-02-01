package sync.slamtalk.map.mapper;

import org.springframework.stereotype.Component;
import sync.slamtalk.map.dto.BasketballCourtDto;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.entity.BasketballCourt;

@Component
public class BasketballCourtMapper {

    // entity -> dto 변환
    public BasketballCourtDto toDto(BasketballCourt basketballCourt) {
        if (basketballCourt == null) {
            return null;
        }
        return new BasketballCourtDto(
                basketballCourt.getCourtId(),
                basketballCourt.getCourtName(),
                basketballCourt.getAddress(),
                basketballCourt.getLatitude(),
                basketballCourt.getLongitude()
        );
    }

    public BasketballCourtDto toFullDto(BasketballCourt basketballCourt) {
        if (basketballCourt == null) {
            return null;
        }
        return new BasketballCourtDto(
                basketballCourt.getCourtId(),
                basketballCourt.getCourtName(),
                basketballCourt.getAddress(),
                basketballCourt.getLatitude(),
                basketballCourt.getLongitude(),
                basketballCourt.getCourtType(),
                basketballCourt.getIndoorOutdoor(),
                basketballCourt.getCourtSize(),
                basketballCourt.getHoopCount(),
                basketballCourt.getNightLighting(),
                basketballCourt.getOpeningHours(),
                basketballCourt.getFee(),
                basketballCourt.getParkingAvailable(),
                basketballCourt.getPhoneNum(),
                basketballCourt.getWebsite(),
                basketballCourt.getConvenience(),
                basketballCourt.getAdditionalInfo(),
                basketballCourt.getPhotoUrl(),
                basketballCourt.getInformerid()
        );
    }

    public BasketballCourt toEntity(BasketballCourtDto dto, Long userId) {
        if (dto == null) {
            return null;
        }

        return BasketballCourt.builder()
                .courtName((dto.getCourtName()))
                .address((dto.getAddress()))
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .courtType(dto.getCourtType())
                .indoorOutdoor(dto.getIndoorOutdoor())
                .courtSize(dto.getCourtSize())
                .hoopCount(dto.getHoopCount())
                .nightLighting(dto.getNightLighting())
                .openingHours(dto.getOpeningHours())
                .fee(dto.getFee())
                .parkingAvailable(dto.getParkingAvailable())
                .phoneNum((dto.getPhoneNum()))
                .website((dto.getWebsite()))
                .convenience((dto.getConvenience()))
                .additionalInfo(dto.getAdditionalInfo())
                .photoUrl(dto.getPhotoUrl())
                .adminStatus(AdminStatus.STAND) // 대기 상태
                .informerid(userId)
                .build();

    }
}
