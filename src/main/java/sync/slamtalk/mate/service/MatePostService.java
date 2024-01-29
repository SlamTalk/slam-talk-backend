package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.dto.MateFormDTO;
import sync.slamtalk.mate.dto.MatePostApplicantDTO;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.MatePostEntityToDtoMapper;
import sync.slamtalk.mate.repository.MatePostRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static sync.slamtalk.mate.error.MateErrorResponseCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatePostService {

    private final MatePostRepository matePostRepository;
    private final ParticipantService participantService;

    // * MatePost를 저장한다.
    public long registerMatePost(MatePost matePost){
        MatePost result = matePostRepository.save(matePost);
        return result.getMatePostId(); // * 저장된 게시글의 아이디를 반환한다.
    }

    /**
     * 메이트찾기 게시글 조회
     * 게시글 ID를 이용해서 저장된 게시글을 불러온다.
     * 게시글을 DTO로 변환하여 반환한다.
     */
    public MateFormDTO getMatePost(long matePostId){

        Optional<MatePost> optionalPost = matePostRepository.findById(matePostId);
        if(!optionalPost.isPresent()){
            throw new BaseException(MATE_POST_NOT_FOUND);
        }
        MatePost post = optionalPost.get();

        List<MatePostApplicantDTO> participantsToArrayList = participantService.getParticipants(matePostId);
        MatePostEntityToDtoMapper mapper = new MatePostEntityToDtoMapper();
        List<String> skillList = mapper.toSkillLevelTypeList(post);

        MateFormDTO mateFormDTO = MateFormDTO.builder()
                .matePostId(post.getMatePostId())
                .writerId(post.getWriterId())
                .title(post.getTitle())
                .content(post.getContent())
                .startScheduledTime(post.getStartScheduledTime())
                .endScheduledTime(post.getEndScheduledTime())
                .locationDetail(post.getLocationDetail())
                .skillLevelList(skillList)
                .maxParticipantsCenters(post.getMaxParticipantsCenters())
                .currentParticipantsCenters(post.getCurrentParticipantsCenters())
                .maxParticipantsGuards(post.getMaxParticipantsGuards())
                .currentParticipantsGuards(post.getCurrentParticipantsGuards())
                .maxParticipantsForwards(post.getMaxParticipantsForwards())
                .currentParticipantsForwards(post.getCurrentParticipantsForwards())
                .maxParticipantsOthers(post.getMaxParticipantsOthers())
                .currentParticipantsOthers(post.getCurrentParticipantsOthers())
                .participants(participantsToArrayList)
                .build();
        return mateFormDTO;
    }

    /**
     * 메이트찾기 게시글 삭제
     * 게시글 ID를 이용해서 저장된 게시글을 삭제한다.(soft delete)
     * 해당 게시글에 속한 참여자 목록도 soft delete 한다.
     */
    public boolean deleteMatePost(long matePostId){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow();
        post.softDeleteMatePost();
        return true;
    }

    /**
     * 메이트찾기 게시글 수정
     * 수정 가능한 항목 : 제목, 내용, 예정된 시간, 상세 시합 장소, 스킬 레벨, 모집 포지션 별 최대 인원 수
     * 모집 포지션 별 최대 인원 수는 필수 기입 사항. 변동사항이 없더라도 기존의 최대 인원 수를 기입해야 함.
     */
    public boolean updateMatePost(long matePostId, MateFormDTO mateFormDTO){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow();
        String content = mateFormDTO.getContent();
        String title = mateFormDTO.getTitle();
        String locationDetail = mateFormDTO.getLocationDetail();
        LocalDateTime startScheduledTime = mateFormDTO.getStartScheduledTime();
        LocalDateTime endScheduledTime = mateFormDTO.getEndScheduledTime();
        RecruitedSkillLevelType skillLevel = mateFormDTO.getSkillLevel();
        Integer maxParticipantsCenters = mateFormDTO.getMaxParticipantsCenters();
        Integer maxParticipantsGuards = mateFormDTO.getMaxParticipantsGuards();
        Integer maxParticipantsForwards = mateFormDTO.getMaxParticipantsForwards();
        Integer maxParticipantsOthers = mateFormDTO.getMaxParticipantsOthers();

        if(content != null && !content.equals("")){ // * 내용이 비어있지 않다면
            post.updateContent(content);
        }

        if(title != null && !title.equals("")){ // * 제목이 비어있지 않다면
            post.updateTitle(title);
        }

        if(locationDetail != null && !locationDetail.equals("")){ // * 상세 시합 장소가 비어있지 않다면
            post.updateLocationDetail(locationDetail);
        }

        if(startScheduledTime != null){
            post.updateStartScheduledTime(startScheduledTime);
        }

        if(endScheduledTime != null){
            post.updateEndScheduledTime(endScheduledTime);
        }

        if(skillLevel != null){
            post.updateSkillLevel(skillLevel);
        }

        if(maxParticipantsCenters != null){
            if(post.getCurrentParticipantsCenters() > maxParticipantsCenters){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsCenters(maxParticipantsCenters);
        }

        if(maxParticipantsGuards != null){
            if(post.getCurrentParticipantsGuards() > maxParticipantsGuards){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsGuards(maxParticipantsGuards);
        }

        if(maxParticipantsForwards != null){
            if(post.getCurrentParticipantsForwards() > maxParticipantsForwards){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsForwards(maxParticipantsForwards);
        }

        if(maxParticipantsOthers != null){
            if(post.getCurrentParticipantsOthers() > maxParticipantsOthers){
                throw new BaseException(DECREASE_POSITION_NOT_AVAILABLE);
            }
            post.updateMaxParticipantsOthers(maxParticipantsOthers);
        }

        return true;
    }

    public List<MatePostDTO> getMatePostsByCurser(String cursorStr, int limit) {
        LocalDateTime cursor = LocalDateTime.parse(cursorStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        log.debug("cursor: {}", cursor);
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<MatePost> listedMatePosts = matePostRepository.findByCreatedAtLessThanOrderByCreatedAtDesc(cursor, pageable);
        log.debug("listedMatePosts: {}", listedMatePosts);
        List<MatePostDTO> response = listedMatePosts.stream().map(MatePostEntityToDtoMapper::toMatePostDto).collect(Collectors.toList());
        return response;
    }

}
