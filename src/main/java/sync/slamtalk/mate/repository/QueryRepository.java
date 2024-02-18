package sync.slamtalk.mate.repository;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.dto.*;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.team.dto.TeamSearchCondition;
import sync.slamtalk.team.dto.ToApplicantDto;
import sync.slamtalk.team.dto.UnrefinedTeamMatchingDto;
import sync.slamtalk.team.entity.TeamApplicant;
import sync.slamtalk.team.entity.TeamMatching;
import sync.slamtalk.user.entity.QUser;
import sync.slamtalk.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static sync.slamtalk.mate.entity.QMatePost.matePost;
import static sync.slamtalk.mate.entity.QParticipant.participant;
import static sync.slamtalk.mate.entity.QTeam.team;
import static sync.slamtalk.team.entity.QTeamApplicant.teamApplicant;
import static sync.slamtalk.team.entity.QTeamMatching.teamMatching;
import static sync.slamtalk.user.entity.QUser.user;

@Slf4j
@Repository
public class QueryRepository {

    private EntityManager em;
    private JPAQueryFactory queryFactory;
    private EntityToDtoMapper entityToDtoMapper;

    public QueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
        this.entityToDtoMapper = new EntityToDtoMapper();
    }

    public List<UnrefinedMatePostDTO> findMatePostList(MateSearchCondition condition) {
        return queryFactory
                .select(bean(UnrefinedMatePostDTO.class,
                        matePost.writer.id.as("writerId"),
                        matePost.writer.nickname.as("writerNickname"),
                        matePost.writer.imageUrl.as("imageUrl"),
                        matePost.matePostId,
                        matePost.title,
                        matePost.content,
                        matePost.scheduledDate,
                        matePost.startTime,
                        matePost.endTime,
                        matePost.location,
                        matePost.locationDetail,
                        matePost.skillLevel,
                        matePost.recruitmentStatus,
                        matePost.maxParticipantsCenters,
                        matePost.currentParticipantsCenters,
                        matePost.maxParticipantsGuards,
                        matePost.currentParticipantsGuards,
                        matePost.maxParticipantsForwards,
                        matePost.currentParticipantsForwards,
                        matePost.maxParticipantsOthers,
                        matePost.currentParticipantsOthers,
                        matePost.createdAt
                        )
                )
                .from(matePost)
                .where(eqLocation("matePost", condition.getLocation()),
                        eqPosition(condition.getPosition()),
                        eqSkillLevel("matePost", condition.getSkillLevel()),
                        ltCreatedAt("matePost", condition.getCursorTime()),
                        beforeScheduledTime("matePost", condition.isIncludingExpired()),
                        matePost.isDeleted.eq(false)
                )
                .orderBy(matePost.createdAt.desc())
                .limit(10)
                .fetch();
    }

    public List<FromParticipantDto> findParticipantByMatePostId(long matePostId) {
        return queryFactory
                .select(
                        bean(FromParticipantDto.class,
                                participant.participantTableId.as("participantTableId"),
                                participant.matePost.matePostId.as("matePostId"),
                                participant.participantId.as("participantId"),
                                participant.participantNickname.as("participantNickname"),
                                participant.applyStatus,
                                participant.position,
                                participant.skillLevel
                        )
                )
                .from(participant)
                .where(
                        eqMatePostId(matePostId),
                        participant.isDeleted.eq(false)
                )
                .orderBy(participant.createdAt.asc())
                .fetch();
    }

    public List<UnrefinedTeamMatchingDto> findTeamMatchingList(TeamSearchCondition condition) {
        return queryFactory
                .select(bean(UnrefinedTeamMatchingDto.class,
                teamMatching.teamMatchingId,
                teamMatching.teamName,
                teamMatching.writer.id.as("writerId"),
                teamMatching.writer.nickname.as("writerNickname"),
                teamMatching.writer.imageUrl.as("writerImageUrl"),
                teamMatching.opponentId,
                teamMatching.opponentNickname,
                teamMatching.opponentTeamName,
                teamMatching.title,
                teamMatching.content,
                teamMatching.location,
                teamMatching.locationDetail,
                teamMatching.numberOfMembers,
                teamMatching.skillLevel,
                teamMatching.scheduledDate,
                teamMatching.startTime,
                teamMatching.endTime,
                teamMatching.createdAt,
                teamMatching.recruitmentStatus
                        )
                )
                .from(teamMatching)
                .where(eqLocation("teamMatching", condition.getLocation()),
                        eqSkillLevel("teamMatching", condition.getSkillLevel()),
                        ltCreatedAt("teamMatching", condition.getCursorTime()),
                        beforeScheduledTime("teamMatching", condition.isIncludingExpired()),
                        eqNumberOfVersus(condition.getNov()),
                        teamMatching.isDeleted.eq(false)
                )
                .orderBy(teamMatching.createdAt.desc())
                .limit(10)
                .fetch();
    }


    public List<ToApplicantDto> findApplicantListByTeamMatchingId(long teamMatchingId) {
        return queryFactory
                .select(
                        bean(ToApplicantDto.class,
                                teamApplicant.teamApplicantTableId,
                                teamApplicant.applicantId,
                                teamApplicant.applicantNickname,
                                teamApplicant.teamMatching.teamMatchingId.as("teamMatchingId"),
                                teamApplicant.applyStatus,
                                teamApplicant.teamName,
                                teamApplicant.skillLevel
                        )
                )
                .from(teamApplicant)
                .where(
                        eqTeamMatchingId(teamMatchingId),
                        teamApplicant.isDeleted.eq(false)
                )
                .orderBy(teamApplicant.createdAt.asc())
                .fetch();
    }

    private BooleanExpression eqMatePostId(Long matePostId) {
        if(matePostId != null){
            return participant.matePost.matePostId.eq(matePostId);
        } else {
            return null;
        }
    }

    private BooleanExpression eqTeamMatchingId(Long teamMatchingId) {
        if(teamMatchingId != null){
            return teamApplicant.teamMatching.teamMatchingId.eq(teamMatchingId);
        } else {
            return null;
        }
    }

    private BooleanExpression eqSkillLevel(String entityType, SkillLevelType skillLevel) {
        if(skillLevel != null){
            if(entityType.equals("matePost")){
                if(skillLevel.equals(SkillLevelType.BEGINNER)){
                    return matePost.skillLevelBeginner.eq(true);
                } else if(skillLevel.equals(SkillLevelType.LOW)){
                    return matePost.skillLevelLow.eq(true);
                } else if(skillLevel.equals(SkillLevelType.MIDDLE)){
                    return matePost.skillLevelMiddle.eq(true);
                } else if(skillLevel.equals(SkillLevelType.HIGH)){
                    return matePost.skillLevelHigh.eq(true);
                } else {
                    return null;
                }
            }else if(entityType.equals("teamMatching")){
                if(skillLevel.equals(SkillLevelType.BEGINNER)){
                    return teamMatching.skillLevelBeginner.eq(true);
                } else if(skillLevel.equals(SkillLevelType.LOW)){
                    return teamMatching.skillLevelLow.eq(true);
                } else if(skillLevel.equals(SkillLevelType.MIDDLE)){
                    return teamMatching.skillLevelMiddle.eq(true);
                } else if(skillLevel.equals(SkillLevelType.HIGH)){
                    return teamMatching.skillLevelHigh.eq(true);
                } else {
                    return null;
                }
            } else {
                log.debug("eqSkillLevel : entityType is not matePost or teamMatching");
                return null;
            }
        } else {
            return null;
        }
    }

    private BooleanExpression eqLocation(String entityType, String location) {
        if(location != null){
            if(entityType.equals("matePost")){
                return matePost.location.eq(location);
            } else if(entityType.equals("teamMatching")){
                return teamMatching.location.eq(location);
            } else {
                log.debug("eqLocation : entityType is not matePost or teamMatching");
                return null;
            }
        } else {
            log.debug("eqLocation : location is null");
            return null;
        }
    }

    private BooleanExpression eqPosition(PositionType position) {
        if(position != null){
            if(position.equals(PositionType.CENTER)){
                return matePost.maxParticipantsCenters.gt(matePost.currentParticipantsCenters).or(matePost.maxParticipantsOthers.gt(matePost.currentParticipantsOthers));
            } else if(position.equals(PositionType.GUARD)){
                return matePost.maxParticipantsGuards.gt(matePost.currentParticipantsGuards).or(matePost.maxParticipantsOthers.gt(matePost.currentParticipantsOthers));
            } else if(position.equals(PositionType.FORWARD)){
                return matePost.maxParticipantsForwards.gt(matePost.currentParticipantsForwards).or(matePost.maxParticipantsOthers.gt(matePost.currentParticipantsOthers));
            }else {
                return null;
            }
        } else {
            return null;
        }
    }

    private BooleanExpression beforeScheduledTime(String entityType, boolean includingExpired) {
        if(includingExpired == false){
            if(entityType.equals("matePost")){
                BooleanExpression isBeforeScheduledDate = matePost.scheduledDate.after(LocalDate.now());

                BooleanExpression isTodayAndBeforeScheduledTime = matePost.scheduledDate.eq(LocalDate.now())
                        .and(matePost.startTime.after(LocalTime.now()));

                return isBeforeScheduledDate.or(isTodayAndBeforeScheduledTime);
            }else if(entityType.equals("teamMatching")){
                BooleanExpression isBeforeScheduledDate = teamMatching.scheduledDate.after(LocalDate.now());

                BooleanExpression isTodayAndBeforeScheduledTime = teamMatching.scheduledDate.eq(LocalDate.now())
                        .and(teamMatching.startTime.after(LocalTime.now()));

                return isBeforeScheduledDate.or(isTodayAndBeforeScheduledTime);
            } else {
                log.debug("beforeScheduledTime : entityType is not matePost or teamMatching");
                return null;
            }
        } else{
            return null;
        }
    }

    private BooleanExpression ltCreatedAt(String entityType, LocalDateTime cursorTime) {
        if(cursorTime != null){
            if(entityType.equals("matePost")){
                return matePost.createdAt.lt(cursorTime);
            } else if(entityType.equals("teamMatching")){
                return teamMatching.createdAt.lt(cursorTime);
            } else {
                log.debug("ltCreatedAt : entityType is not matePost or teamMatching");
                return null;
            }
        } else {
            return null;
        }
    }

    private BooleanExpression eqNumberOfVersus(String numberOfMembers) {
        if(numberOfMembers != null){
            return teamMatching.numberOfMembers.eq(numberOfMembers);
        } else {
            return null;
        }
    }
}
