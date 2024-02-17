package sync.slamtalk.mate.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.dto.*;
import sync.slamtalk.mate.entity.*;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;
import sync.slamtalk.user.entity.QUser;
import sync.slamtalk.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static sync.slamtalk.mate.entity.QMatePost.matePost;
import static sync.slamtalk.mate.entity.QParticipant.participant;
import static sync.slamtalk.mate.entity.QTeam.team;
import static sync.slamtalk.user.entity.QUser.user;

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
                .where(eqLocation(condition.getLocation()),
                        eqPosition(condition.getPosition()),
                        eqSkillLevel(condition.getSkillLevel()),
                        ltCreatedAt(condition.getCursorTime()),
                        beforeScheduledTime(),
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
                .orderBy(matePost.createdAt.desc())
                .limit(10)
                .fetch();
    }

    private BooleanExpression eqMatePostId(Long matePostId) {
        if(matePostId != null){
            return participant.matePost.matePostId.eq(matePostId);
        } else {
            return null;
        }
    }
    private BooleanExpression eqSkillLevel(SkillLevelType skillLevel) {
        if(skillLevel != null){
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
        } else {
            return null;
        }
    }

    private BooleanExpression eqLocation(String location) {
        if(location != null){
            return matePost.location.eq(location);
        } else {
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

    private BooleanExpression beforeScheduledTime() {
        DateTimeExpression<LocalDateTime> scheduledStartTime = Expressions.dateTimeTemplate(LocalDateTime.class, "{0}T{1}", matePost.scheduledDate, matePost.startTime);
        return scheduledStartTime.after(LocalDateTime.now());
    }

    private BooleanExpression ltCreatedAt(LocalDateTime cursorTime) {
        if(cursorTime == null){
            return null;
        }
        return matePost.createdAt.lt(cursorTime);
    }

    public List<Tuple> queryForTest(TestCondition condition){
        return queryFactory
                .select(team.id, team.name, team.location, team.sport)
                .from(team)
                .where(eqLocation(condition.getLocation()))
                .fetch();
    }

}
