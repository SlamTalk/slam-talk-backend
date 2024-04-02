    package sync.slamtalk.map.service;

    import java.util.Optional;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.multipart.MultipartFile;
    import sync.slamtalk.chat.entity.ChatRoom;
    import sync.slamtalk.chat.repository.ChatRoomRepository;
    import sync.slamtalk.common.BaseException;
    import sync.slamtalk.common.s3bucket.repository.AwsS3RepositoryImpl;
    import sync.slamtalk.map.dto.BasketballCourtAdminRequestDTO;
    import sync.slamtalk.map.dto.BasketballCourtErrorResponse;
    import sync.slamtalk.map.dto.BasketballCourtRequestDTO;
    import sync.slamtalk.map.entity.AdminStatus;
    import sync.slamtalk.map.entity.BasketballCourt;
    import sync.slamtalk.map.entity.Fee;
    import sync.slamtalk.map.entity.NightLighting;
    import sync.slamtalk.map.entity.OpeningHours;
    import sync.slamtalk.map.entity.ParkingAvailable;
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
        private final ChatRoomRepository chatRoomRepository;

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

            updateCourtDetails(court, basketballCourtRequestDTO, photoUrl);

            return basketballCourtRepository.save(court);
        }


        @Transactional
        public BasketballCourt updateCourt(Long courtId, BasketballCourtAdminRequestDTO basketballCourtAdminRequestDTO) {
            BasketballCourt court = basketballCourtRepository.findById(courtId)
                    .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.MAP_FAIL));

            updateCourtAdminDetails(court, basketballCourtAdminRequestDTO, court.getPhotoUrl());

            // AdminStatus 변경
            court.updateAdminStatus(AdminStatus.ACCEPT);

            // 채팅방에 농구장 매핑
            ChatRoom chatRoom = court.getChatroom();
            chatRoom.setBasketballCourt(court);

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
                NightLighting nightLighting = NightLighting.fromString(requestDTO.getNightLighting());
                court.updateNightLighting(nightLighting);
            }
            if (requestDTO.getOpeningHours() != null) {
                OpeningHours openingHours = OpeningHours.fromString(requestDTO.getOpeningHours());
                court.updateOpeningHours(openingHours);
            }
            if (requestDTO.getFee() != null) {
                Fee fee = Fee.fromString(requestDTO.getFee());
                court.updateFee(fee);
            }
            if (requestDTO.getParkingAvailable() != null) {
                ParkingAvailable parkingAvailable = ParkingAvailable.fromString(requestDTO.getParkingAvailable());
                court.updateParkingAvailable(parkingAvailable);
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

        private void updateCourtAdminDetails(BasketballCourt court, BasketballCourtAdminRequestDTO basketballCourtAdminRequestDTO, String photoUrl) {
            if (basketballCourtAdminRequestDTO.getCourtType() != null) {
                court.updateCourtType(basketballCourtAdminRequestDTO.getCourtType());
            }
            if (basketballCourtAdminRequestDTO.getIndoorOutdoor() != null) {
                court.updateIndoorOutDoor(basketballCourtAdminRequestDTO.getIndoorOutdoor());
            }
            if (basketballCourtAdminRequestDTO.getCourtSize() != null) {
                court.updateCourtSize(basketballCourtAdminRequestDTO.getCourtSize());
            }
            if (basketballCourtAdminRequestDTO.getHoopCount() != null) {
                court.updateHoopCount(basketballCourtAdminRequestDTO.getHoopCount());
            }
            if (basketballCourtAdminRequestDTO.getNightLighting() != null) {
                NightLighting nightLighting = NightLighting.fromString(basketballCourtAdminRequestDTO.getNightLighting());
                court.updateNightLighting(nightLighting);
            }
            if (basketballCourtAdminRequestDTO.getOpeningHours() != null) {
                OpeningHours openingHours = OpeningHours.fromString(basketballCourtAdminRequestDTO.getOpeningHours());
                court.updateOpeningHours(openingHours);
            }
            if (basketballCourtAdminRequestDTO.getFee() != null) {
                Fee fee = Fee.fromString(basketballCourtAdminRequestDTO.getFee());
                court.updateFee(fee);
            }
            if (basketballCourtAdminRequestDTO.getParkingAvailable() != null) {
                ParkingAvailable parkingAvailable = ParkingAvailable.fromString(basketballCourtAdminRequestDTO.getParkingAvailable());
                court.updateParkingAvailable(parkingAvailable);
            }
            if (basketballCourtAdminRequestDTO.getPhoneNum() != null) {
                court.updatePhoneNum(basketballCourtAdminRequestDTO.getPhoneNum());
            }
            if (basketballCourtAdminRequestDTO.getWebsite() != null) {
                court.updateWebsite(basketballCourtAdminRequestDTO.getWebsite());
            }
            if (basketballCourtAdminRequestDTO.getConvenience() != null) {
                court.updateConvenience(basketballCourtAdminRequestDTO.getConvenience());
            }
            if (basketballCourtAdminRequestDTO.getAdditionalInfo() != null) {
                court.updateAdditionalInfo(basketballCourtAdminRequestDTO.getAdditionalInfo());
            }
            if (photoUrl != null) {
                court.updatePhotoUrl(photoUrl);
            }
            if (basketballCourtAdminRequestDTO.getChatroomId() != null) {
                Optional<ChatRoom> chatRoom = chatRoomRepository.findById(basketballCourtAdminRequestDTO.getChatroomId());
                chatRoom.ifPresent(court::updateChatroom);
            }
        }
    }
