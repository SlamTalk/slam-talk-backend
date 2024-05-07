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
    import sync.slamtalk.map.entity.*;
    import sync.slamtalk.map.entity.RegistrationStatus;
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

        /**
         * 사용자에게 농구장을 제보 받는 기능을 수행합니다.
         * <p>
         *     사용자에게 농구장 정보를 제보 받아 데이터베이스에 대기 상태로 저장합니다.
         * </p>
         *
         * @param basketballCourtRequestDTO 사용자가 제보한 농구장 정보
         * @param file 사용자가 제보한 농구장 사진, 없는 경우 null
         * @param userId 제보한 사용자 ID
         * @return 데이터베이스에 저장된 농구장 Entity
         */
        @Transactional
        public BasketballCourt createBasketballCourtReport(BasketballCourtRequestDTO basketballCourtRequestDTO, MultipartFile file,
                                                           Long userId) {

            String photoUrl = "";
            if (file != null && !file.isEmpty()) {
                photoUrl = awsS3Repository.uploadFile(file);
            }

            BasketballCourt court = basketballCourtMapper.toEntity(basketballCourtRequestDTO, photoUrl, userId);
            return basketballCourtRepository.save(court);
        }

        /**
         * 제보된 농구장 정보를 수정하는 기능을 수행합니다.
         * <p>
         *     제보자 인증을 거쳐 제보자가 제보한 농구장 정보를 업데이트합니다.
         * </p>
         * @param courtId 사용자에 의해 제보된 대기 상태의 농구장 ID
         * @param basketballCourtRequestDTO 수정될 농구장 정보
         * @param file 새로운 농구장 사진, 없는 경우 기존 이미지
         * @param userId 제보자의 사용자 ID
         * @return 업데이트된 농구장 Entity
         * @throws BaseException ID에 해당하는 농구장이 존재하지 않을 때, 사용자가 수정 권한이 없는 경우 예외 발생
         */
        @Transactional
        public BasketballCourt updateSubmittedBasketballCourtReport(Long courtId, BasketballCourtRequestDTO basketballCourtRequestDTO,
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

            applyUserUpdatesToBasketballCourt(court, basketballCourtRequestDTO, photoUrl);

            return basketballCourtRepository.save(court);
        }


        /**
         * 관리자가 대기 상태의 농구장 정보를 승인하는 기능을 수행합니다.
         * <p>
         *     관리자가 대기 상태의 농구장 정보를 추가하거나 수정하고 승인하는 기능을 수행합니다.
         * </p>
         * @param courtId 승인할 농구장 ID
         * @param basketballCourtAdminRequestDTO 추가하거나 수정할 농구장 정보
         * @return 승인된 농구장 Entity
         * @throws BaseException ID에 해당하는 농구장이 존재하지 않을 때, 예외 발생
         */
        @Transactional
        public BasketballCourt approveBasketballCourtInfoUpdate(Long courtId, BasketballCourtAdminRequestDTO basketballCourtAdminRequestDTO) {
            BasketballCourt court = basketballCourtRepository.findById(courtId)
                    .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.MAP_FAIL));

            administrateBasketballCourtUpdates(court, basketballCourtAdminRequestDTO, court.getPhotoUrl());

            // AdminStatus 변경
            court.updateRegistrationStatus(RegistrationStatus.ACCEPT);

            // 채팅방에 농구장 매핑
            ChatRoom chatRoom = court.getChatroom();
            chatRoom.setBasketballCourt(court);

            return basketballCourtRepository.save(court);
        }

        /**
         * 괸리자가 대기 상태의 농구장을 거절하는 기능을 수행합니다.
         * @param courtId 거절할 농구장 ID
         * @return 거절된 농구장 Entity
         */
        @Transactional
        public BasketballCourt rejectBasketballCourUpdate(Long courtId) {
            BasketballCourt court = basketballCourtRepository.findById(courtId)
                    .orElseThrow(() -> new BaseException(BasketballCourtErrorResponse.MAP_FAIL));

            // AdminStatus 변경
            court.updateRegistrationStatus(RegistrationStatus.REJECT);

            return basketballCourtRepository.save(court);
        }

        /**
         * 사용자에게 정보를 제공받아 농구장 정보를 업데이트하는 기능을 수행합니다.
         *<p>
         *     모든 필드는 선택적이며, 제공된 경우에만 업데이트됩니다.
         *</p>
         * @param court 업데이트할 농구장 Entity
         * @param requestDTO 사용자 제보 기반 업데이트할 농구장 정보
         * @param photoUrl 업데이트할 농구장 사진, 없는 경우 기존 이미지
         */
        private void applyUserUpdatesToBasketballCourt(BasketballCourt court, BasketballCourtRequestDTO requestDTO, String photoUrl) {
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

        /**
         * 관리자 정보를 기반으로 상세 정보를 업데이트합니다.
         * @param court 업데이트할 농구장 Entity
         * @param basketballCourtAdminRequestDTO 관리자 정보 기반 업데이트할 농구장 정보
         * @param photoUrl 업데이트할 농구장 사진, 없는 경우 기존 이미지
         */
        private void administrateBasketballCourtUpdates(BasketballCourt court, BasketballCourtAdminRequestDTO basketballCourtAdminRequestDTO, String photoUrl) {
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
