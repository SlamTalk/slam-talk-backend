package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.mate.dto.MateFormDTO;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.entity.Participant;
import sync.slamtalk.mate.entity.SkillLevelType;
import sync.slamtalk.mate.repository.MatePostRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatePostService {

    private final MatePostRepository matePostRepository;

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

        MatePost post = matePostRepository.findById(matePostId).orElseThrow();
        List<Participant> participants = post.getParticipants();
        ArrayList<Participant> participantsToArrayList = new ArrayList<>(participants);
        MateFormDTO mateFormDTO = MateFormDTO.builder()
                .userId(post.getWriterId())
                .title(post.getTitle())
                .content(post.getContent())
                .scheduledTime(post.getScheduledTime())
                .locationDetail(post.getLocationDetail())
                .skillLevel(post.getSkillLevel())
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

    public void addParticipant(long matePostId, Participant participant){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow();
        post.addParticipant(participant);
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
     * 모집 글에 신청자가 한 명 이라도 있으면 수정 불가능하게 하기
     * 수정 가능한 항목 : 제목, 내용, 예정된 시간, 상세 시합 장소, 스킬 레벨, 모집 포지션 별 최대 인원 수
     * 모집 포지션 별 최대 인원 수는 필수 기입 사항. 변동사항이 없더라도 기존의 최대 인원 수를 기입해야 함.
     */
    public boolean updateMatePost(long matePostId, MateFormDTO mateFormDTO){
        MatePost post = matePostRepository.findById(matePostId).orElseThrow();
        String content = mateFormDTO.getContent();
        String title = mateFormDTO.getTitle();
        String locationDetail = mateFormDTO.getLocationDetail();
        LocalDateTime scheduledTime = mateFormDTO.getScheduledTime();
        SkillLevelType skillLevel = mateFormDTO.getSkillLevel();
        int maxParticipantsCenters = mateFormDTO.getMaxParticipantsCenters();
        int maxParticipantsGuards = mateFormDTO.getMaxParticipantsGuards();
        int maxParticipantsForwards = mateFormDTO.getMaxParticipantsForwards();
        int maxParticipantsOthers = mateFormDTO.getMaxParticipantsOthers();

        if(content != null){
            post.updateContent(content);
        }

        if(title != null){
            post.updateTitle(title);
        }

        if(locationDetail != null){
            post.updateLocationDetail(locationDetail);
        }

        if(scheduledTime != null){
            post.updateScheduledTime(scheduledTime);
        }

        if(skillLevel != null){
            post.updateSkillLevel(skillLevel);
        }

        if(maxParticipantsCenters != 0){
            post.updateMaxParticipantsCenters(maxParticipantsCenters);
        }

        if(maxParticipantsGuards != 0){
            post.updateMaxParticipantsGuards(maxParticipantsGuards);
        }

        if(maxParticipantsForwards != 0){
            post.updateMaxParticipantsForwards(maxParticipantsForwards);
        }

        if(maxParticipantsOthers != 0){
            post.updateMaxParticipantsOthers(maxParticipantsOthers);
        }

        return true;
    }
}