    package sync.slamtalk.map.service;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import sync.slamtalk.common.BaseException;
    import sync.slamtalk.map.dto.BasketballCourtDto;
    import sync.slamtalk.map.dto.BasketballCourtErrorResponse;
    import sync.slamtalk.map.entity.AdminStatus;
    import sync.slamtalk.map.entity.BasketballCourt;
    import sync.slamtalk.map.mapper.BasketballCourtMapper;
    import sync.slamtalk.map.repository.BasketballCourtRepository;

    @Service
    @RequiredArgsConstructor
    public class ReportBasketballCourtService {
        private final BasketballCourtRepository basketballCourtRepository;
        private final BasketballCourtMapper basketballCourtMapper;

        @Transactional
        public BasketballCourt reportCourt(BasketballCourtDto basketballCourtDto, Long userId) {
            BasketballCourt court = basketballCourtMapper.toEntity(basketballCourtDto, userId);
            return basketballCourtRepository.save(court);
        }

        @Transactional
        public BasketballCourt updateCourt(Long courtId, BasketballCourtDto basketballCourtDto) {
            BasketballCourt court = basketballCourtRepository.findById(courtId)
                    .orElseThrow(()->new BaseException(BasketballCourtErrorResponse.MAP_FAIL));

            // null이 아닌 값만 입력 필드 업데이트
            if (basketballCourtDto.getCourtType() != null) {
                court.updateCourtType(basketballCourtDto.getCourtType());
            }

            if (basketballCourtDto.getIndoorOutdoor() != null) {
                court.updateIndoorOutDoor(basketballCourtDto.getIndoorOutdoor());
            }

            if (basketballCourtDto.getCourtSize() != null) {
                court.updateCourtSize(basketballCourtDto.getCourtSize());
            }

            if (basketballCourtDto.getHoopCount() != null) {
                court.updateHoopCount(basketballCourtDto.getHoopCount());
            }

            if (basketballCourtDto.getNightLighting() != null) {
                court.updateNightLighting(basketballCourtDto.getNightLighting());
            }

            if (basketballCourtDto.getOpeningHours() != null) {
                court.updateOpeningHours(basketballCourtDto.getOpeningHours());
            }

            if (basketballCourtDto.getFee() != null) {
                court.updateFee(basketballCourtDto.getFee());
            }

            if (basketballCourtDto.getParkingAvailable() != null) {
                court.updateParkingAvailable(basketballCourtDto.getParkingAvailable());
            }

            if (basketballCourtDto.getPhoneNum() != null) {
                court.updatePhoneNum(basketballCourtDto.getPhoneNum());
            }

            if (basketballCourtDto.getWebsite() != null) {
                court.updateWebsite(basketballCourtDto.getWebsite());
            }

            if (basketballCourtDto.getConvenience() != null) {
                court.updateConvenience(basketballCourtDto.getConvenience());
            }

            if (basketballCourtDto.getAdditionalInfo() != null) {
                court.updateAdditionalInfo(basketballCourtDto.getAdditionalInfo());
            }

            if (basketballCourtDto.getPhotoUrl() != null) {
                court.updatePhotoUrl(basketballCourtDto.getPhotoUrl());
            }

            // AdminStatus 변경
            court.updateAdminStatus(AdminStatus.ACCEPT);

            return basketballCourtRepository.save(court);
        }
    }
