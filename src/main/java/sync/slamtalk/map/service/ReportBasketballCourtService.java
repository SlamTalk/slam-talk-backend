    package sync.slamtalk.map.service;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import sync.slamtalk.common.BaseException;
    import sync.slamtalk.map.dto.BasketballCourtErrorResponse;
    import sync.slamtalk.map.dto.BasketballCourtRequestDTO;
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
        public BasketballCourt reportCourt(BasketballCourtRequestDTO basketballCourtRequestDTO, Long userId) {
            BasketballCourt court = basketballCourtMapper.toEntity(basketballCourtRequestDTO, userId);
            return basketballCourtRepository.save(court);
        }

        @Transactional
        public BasketballCourt updateCourt(Long courtId, BasketballCourtRequestDTO basketballCourtRequestDTO) {
            BasketballCourt court = basketballCourtRepository.findById(courtId)
                    .orElseThrow(()->new BaseException(BasketballCourtErrorResponse.MAP_FAIL));

            // null이 아닌 값만 입력 필드 업데이트
            if (basketballCourtRequestDTO.getCourtType() != null) {
                court.updateCourtType(basketballCourtRequestDTO.getCourtType());
            }

            if (basketballCourtRequestDTO.getIndoorOutdoor() != null) {
                court.updateIndoorOutDoor(basketballCourtRequestDTO.getIndoorOutdoor());
            }

            if (basketballCourtRequestDTO.getCourtSize() != null) {
                court.updateCourtSize(basketballCourtRequestDTO.getCourtSize());
            }

            if (basketballCourtRequestDTO.getHoopCount() != null) {
                court.updateHoopCount(basketballCourtRequestDTO.getHoopCount());
            }

            if (basketballCourtRequestDTO.getNightLighting() != null) {
                court.updateNightLighting(basketballCourtRequestDTO.getNightLighting());
            }

            if (basketballCourtRequestDTO.getOpeningHours() != null) {
                court.updateOpeningHours(basketballCourtRequestDTO.getOpeningHours());
            }

            if (basketballCourtRequestDTO.getFee() != null) {
                court.updateFee(basketballCourtRequestDTO.getFee());
            }

            if (basketballCourtRequestDTO.getParkingAvailable() != null) {
                court.updateParkingAvailable(basketballCourtRequestDTO.getParkingAvailable());
            }

            if (basketballCourtRequestDTO.getPhoneNum() != null) {
                court.updatePhoneNum(basketballCourtRequestDTO.getPhoneNum());
            }

            if (basketballCourtRequestDTO.getWebsite() != null) {
                court.updateWebsite(basketballCourtRequestDTO.getWebsite());
            }

            if (basketballCourtRequestDTO.getConvenience() != null) {
                court.updateConvenience(basketballCourtRequestDTO.getConvenience());
            }

            if (basketballCourtRequestDTO.getAdditionalInfo() != null) {
                court.updateAdditionalInfo(basketballCourtRequestDTO.getAdditionalInfo());
            }

            if (basketballCourtRequestDTO.getPhotoUrl() != null) {
                court.updatePhotoUrl(basketballCourtRequestDTO.getPhotoUrl());
            }

            // AdminStatus 변경
            court.updateAdminStatus(AdminStatus.ACCEPT);

            return basketballCourtRepository.save(court);
        }
    }
