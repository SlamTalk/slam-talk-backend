package sync.slamtalk.map.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import sync.slamtalk.map.dto.BasketballCourtFullResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtReportResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtReportSummaryDTO;
import sync.slamtalk.map.dto.BasketballCourtRequestDTO;
import sync.slamtalk.map.dto.BasketballCourtResponseDTO;
import sync.slamtalk.map.dto.BasketballCourtSummaryDto;
import sync.slamtalk.map.entity.AdminStatus;
import sync.slamtalk.map.entity.BasketballCourt;
import sync.slamtalk.map.entity.Fee;
import sync.slamtalk.map.entity.NightLighting;
import sync.slamtalk.map.entity.OpeningHours;
import sync.slamtalk.map.entity.ParkingAvailable;

@Component
public class BasketballCourtMapper {

    // entity -> dto 변환
    public BasketballCourtSummaryDto toDto(BasketballCourt basketballCourt) {
        if (basketballCourt == null) {
            return null;
        }
        return new BasketballCourtSummaryDto(
                basketballCourt.getId(),
                basketballCourt.getCourtName(),
                basketballCourt.getAddress(),
                basketballCourt.getLatitude(),
                basketballCourt.getLongitude()
        );
    }

    public BasketballCourtReportSummaryDTO toStatusDto(BasketballCourt basketballCourt) {
        if (basketballCourt == null) {
            return null;
        }
        return new BasketballCourtReportSummaryDTO(
                basketballCourt.getId(),
                basketballCourt.getCourtName(),
                basketballCourt.getAddress(),
                basketballCourt.getLatitude(),
                basketballCourt.getLongitude(),
                basketballCourt.getAdminStatus().name()
        );
    }

    public BasketballCourtResponseDTO toFullDto(BasketballCourt basketballCourt) {
        if (basketballCourt == null) {
            return null;
        }

        List<String> convenienceList = basketballCourt.getConvenience() != null
                ? Arrays.asList(basketballCourt.getConvenience().split(","))
                : Collections.emptyList();

        String nightLightingValue = basketballCourt.getNightLighting() != null ? basketballCourt.getNightLighting().getLighting_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값
        String feeValue = basketballCourt.getFee() != null ? basketballCourt.getFee().getFee_type() : "정보 없음"; // Fee 값이 null일 경우를 위한 기본값
        String openingHoursValue = basketballCourt.getOpeningHours() != null ? basketballCourt.getOpeningHours().getOpeningHours_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값
        String parkingAvailableValue = basketballCourt.getParkingAvailable() != null ? basketballCourt.getParkingAvailable().getParkingAvailable_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값

        return new BasketballCourtResponseDTO(
                basketballCourt.getId(),
                basketballCourt.getCourtName(),
                basketballCourt.getAddress(),
                basketballCourt.getLatitude(),
                basketballCourt.getLongitude(),
                basketballCourt.getCourtType(),
                basketballCourt.getIndoorOutdoor(),
                basketballCourt.getCourtSize(),
                basketballCourt.getHoopCount(),
                nightLightingValue,
                openingHoursValue,
                feeValue,
                parkingAvailableValue,
                basketballCourt.getPhoneNum(),
                basketballCourt.getWebsite(),
                convenienceList,
                basketballCourt.getAdditionalInfo(),
                basketballCourt.getPhotoUrl(),
                basketballCourt.getInformerId()
        );
    }

    public BasketballCourtFullResponseDTO toFullChatDto(BasketballCourt basketballCourt) {
        if (basketballCourt == null) {
            return null;
        }

        List<String> convenienceList = basketballCourt.getConvenience() != null
                ? Arrays.asList(basketballCourt.getConvenience().split(","))
                : Collections.emptyList();

        String nightLightingValue = basketballCourt.getNightLighting() != null ? basketballCourt.getNightLighting().getLighting_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값
        String feeValue = basketballCourt.getFee() != null ? basketballCourt.getFee().getFee_type() : "정보 없음"; // Fee 값이 null일 경우를 위한 기본값
        String openingHoursValue = basketballCourt.getOpeningHours() != null ? basketballCourt.getOpeningHours().getOpeningHours_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값
        String parkingAvailableValue = basketballCourt.getParkingAvailable() != null ? basketballCourt.getParkingAvailable().getParkingAvailable_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값

        return new BasketballCourtFullResponseDTO(
                basketballCourt.getId(),
                basketballCourt.getCourtName(),
                basketballCourt.getAddress(),
                basketballCourt.getLatitude(),
                basketballCourt.getLongitude(),
                basketballCourt.getCourtType(),
                basketballCourt.getIndoorOutdoor(),
                basketballCourt.getCourtSize(),
                basketballCourt.getHoopCount(),
                nightLightingValue,
                openingHoursValue,
                feeValue,
                parkingAvailableValue,
                basketballCourt.getPhoneNum(),
                basketballCourt.getWebsite(),
                convenienceList,
                basketballCourt.getAdditionalInfo(),
                basketballCourt.getPhotoUrl(),
                basketballCourt.getInformerId(),
                basketballCourt.getChatroom().getId()
        );
    }



    public BasketballCourtReportResponseDTO toFullStatusDto(BasketballCourt basketballCourt) {
        if (basketballCourt == null) {
            return null;
        }

        List<String> convenienceList = basketballCourt.getConvenience() != null
                ? Arrays.asList(basketballCourt.getConvenience().split(","))
                : Collections.emptyList();

        String nightLightingValue = basketballCourt.getNightLighting() != null ? basketballCourt.getNightLighting().getLighting_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값
        String feeValue = basketballCourt.getFee() != null ? basketballCourt.getFee().getFee_type() : "정보 없음"; // Fee 값이 null일 경우를 위한 기본값
        String openingHoursValue = basketballCourt.getOpeningHours() != null ? basketballCourt.getOpeningHours().getOpeningHours_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값
        String parkingAvailableValue = basketballCourt.getParkingAvailable() != null ? basketballCourt.getParkingAvailable().getParkingAvailable_type() : "정보 없음"; // NightLighting 값이 null일 경우를 위한 기본값

        return new BasketballCourtReportResponseDTO(
                basketballCourt.getId(),
                basketballCourt.getCourtName(),
                basketballCourt.getAddress(),
                basketballCourt.getLatitude(),
                basketballCourt.getLongitude(),
                basketballCourt.getCourtType(),
                basketballCourt.getIndoorOutdoor(),
                basketballCourt.getCourtSize(),
                basketballCourt.getHoopCount(),
                nightLightingValue,
                openingHoursValue,
                feeValue,
                parkingAvailableValue,
                basketballCourt.getPhoneNum(),
                basketballCourt.getWebsite(),
                convenienceList,
                basketballCourt.getAdditionalInfo(),
                basketballCourt.getPhotoUrl(),
                basketballCourt.getInformerId(),
                basketballCourt.getAdminStatus().name()
        );
    }

    public BasketballCourt toEntity(BasketballCourtRequestDTO dto, String photoUrl , Long userId) {
        if (dto == null) {
            return null;
        }

        NightLighting nightLighting = NightLighting.fromString(dto.getNightLighting());
        OpeningHours openingHours = OpeningHours.fromString(dto.getOpeningHours());
        Fee fee = Fee.fromString(dto.getFee());
        ParkingAvailable parkingAvailable = ParkingAvailable.fromString(dto.getParkingAvailable());

        return BasketballCourt.builder()
                .courtName((dto.getCourtName()))
                .address((dto.getAddress()))
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .courtType(dto.getCourtType())
                .indoorOutdoor(dto.getIndoorOutdoor())
                .courtSize(dto.getCourtSize())
                .hoopCount(dto.getHoopCount())
                .nightLighting(nightLighting)
                .openingHours(openingHours)
                .fee(fee)
                .parkingAvailable(parkingAvailable)
                .phoneNum((dto.getPhoneNum()))
                .website((dto.getWebsite()))
                .convenience(dto.getConvenience())
                .additionalInfo(dto.getAdditionalInfo())
                .photoUrl(photoUrl)
                .adminStatus(AdminStatus.STAND) // 대기 상태
                .informerId(userId)
                .build();

    }
}
