    package sync.slamtalk.map.service;

    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.multipart.MultipartFile;
    import sync.slamtalk.common.BaseException;
    import sync.slamtalk.common.s3bucket.repository.AwsS3RepositoryImpl;
    import sync.slamtalk.map.dto.BasketballCourtErrorResponse;
    import sync.slamtalk.map.dto.BasketballCourtRequestDTO;
    import sync.slamtalk.map.entity.AdminStatus;
    import sync.slamtalk.map.entity.BasketballCourt;
    import sync.slamtalk.map.mapper.BasketballCourtMapper;
    import sync.slamtalk.map.repository.BasketballCourtRepository;
    import sync.slamtalk.user.UserRepository;
    import sync.slamtalk.user.entity.User;

    @Service
    @RequiredArgsConstructor
    public class ReportBasketballCourtService {
        private final BasketballCourtRepository basketballCourtRepository;
        private final BasketballCourtMapper basketballCourtMapper;
        private final AwsS3RepositoryImpl awsS3Repository;
        private final UserRepository userRepository;

        @Transactional
        public BasketballCourt reportCourt(BasketballCourtRequestDTO basketballCourtRequestDTO, MultipartFile file,
                                           Long userId) {

            String photoUrl = "";
            if (file != null && !file.isEmpty()) {
                photoUrl = awsS3Repository.uploadFile(file);
            }

            BasketballCourt court = basketballCourtMapper.toEntity(basketballCourtRequestDTO, photoUrl, userId);
            return basketballCourtRepository.save(court);
        }

        @Transactional
        public BasketballCourt editReportCourt(Long courtId, BasketballCourtRequestDTO basketballCourtRequestDTO,
                                               MultipartFile file,
                                               Long userId) {

            System.out.println("service");

            // 농구장 정보 조회
            BasketballCourt court = basketballCourtRepository.findById(courtId)
                    .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.MAP_FAIL));

            // 유저 정보 조회
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.USER_NOT_FOUND));

            // 사용자 검증
            if (!court.getInformerId().equals(user.getId())) {
                throw new BaseException(BasketballCourtErrorResponse.UNAUTHORIZED_USER);
            }

            String photoUrl = court.getPhotoUrl(); // 기존 URL을 기본값으로 설정
            if (file != null && !file.isEmpty()) {
                photoUrl = awsS3Repository.uploadFile(file);
            }

           /* // null이 아닌 값만 입력 필드 업데이트
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
            }*/

            updateCourtDetails(court, basketballCourtRequestDTO, photoUrl);

            // Photo URL 업데이트
            //court.updatePhotoUrl(photoUrl);

            return basketballCourtRepository.save(court);
        }


        @Transactional
        public BasketballCourt updateCourt(Long courtId, BasketballCourtRequestDTO basketballCourtRequestDTO) {
            BasketballCourt court = basketballCourtRepository.findById(courtId)
                    .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.MAP_FAIL));

            /*// null이 아닌 값만 입력 필드 업데이트
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
            }*/

            updateCourtDetails(court, basketballCourtRequestDTO, court.getPhotoUrl());

            // AdminStatus 변경
            court.updateAdminStatus(AdminStatus.ACCEPT);

            return basketballCourtRepository.save(court);
        }

        // 중복된 업데이트 로직을 처리하는 메소드
        private void updateCourtDetails(BasketballCourt court, BasketballCourtRequestDTO requestDTO, String photoUrl) {
            if (requestDTO.getCourtType() != null) {
                court.updateCourtType(requestDTO.getCourtType());
            }
            if (requestDTO.getIndoorOutdoor() != null) {
                court.updateIndoorOutDoor(requestDTO.getIndoorOutdoor());
            }
            if (requestDTO.getCourtSize() != null) {
                court.updateCourtSize(requestDTO.getCourtSize());
            }
            if (requestDTO.getHoopCount() != null) {
                court.updateHoopCount(requestDTO.getHoopCount());
            }
            if (requestDTO.getNightLighting() != null) {
                court.updateNightLighting(requestDTO.getNightLighting());
            }
            if (requestDTO.getOpeningHours() != null) {
                court.updateOpeningHours(requestDTO.getOpeningHours());
            }
            if (requestDTO.getFee() != null) {
                court.updateFee(requestDTO.getFee());
            }
            if (requestDTO.getParkingAvailable() != null) {
                court.updateParkingAvailable(requestDTO.getParkingAvailable());
            }
            if (requestDTO.getPhoneNum() != null) {
                court.updatePhoneNum(requestDTO.getPhoneNum());
            }
            if (requestDTO.getWebsite() != null) {
                court.updateWebsite(requestDTO.getWebsite());
            }
            if (requestDTO.getConvenience() != null) {
                court.updateConvenience(requestDTO.getConvenience());
            }
            if (requestDTO.getAdditionalInfo() != null) {
                court.updateAdditionalInfo(requestDTO.getAdditionalInfo());
            }
            if (photoUrl != null) {
                court.updatePhotoUrl(photoUrl);
            }
        }
    }
