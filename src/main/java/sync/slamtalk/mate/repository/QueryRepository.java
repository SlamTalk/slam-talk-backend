package sync.slamtalk.mate.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import sync.slamtalk.mate.dto.MatePostDTO;
import sync.slamtalk.mate.dto.MateSearchCondition;
import sync.slamtalk.mate.dto.QMatePostDTO;
import sync.slamtalk.mate.entity.PositionType;
import sync.slamtalk.mate.entity.SkillLevelType;
import sync.slamtalk.mate.mapper.EntityToDtoMapper;

import java.time.LocalDateTime;
import java.util.List;

import static sync.slamtalk.mate.entity.QMatePost.matePost;

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

    public List<MatePostDTO> findMatePostList(MateSearchCondition condition) {
        return queryFactory
                .select(new QMatePostDTO(
                        matePost.matePostId,
                        matePost.writer.id,
                        matePost.writer.nickname,
                        matePost.scheduledDate,
                        matePost.startTime,
                        matePost.endTime,
                        matePost.title,
                        matePost.content,
                        matePost.currentParticipantsCenters,
                        matePost.maxParticipantsCenters,
                        matePost.currentParticipantsForwards,
                        matePost.maxParticipantsForwards,
                        matePost.currentParticipantsGuards,
                        matePost.maxParticipantsGuards,
                        matePost.currentParticipantsOthers,
                        matePost.maxParticipantsOthers,
                        matePost.skillLevelBeginner,
                        matePost.skillLevelLow,
                        matePost.skillLevelMiddle,
                        matePost.skillLevelHigh,
                        matePost.recruitmentStatus,
                        matePost.locationDetail,
                        matePost.participants,
                        matePost.createdAt
                ))
                .from(matePost)

                .orderBy(matePost.createdAt.desc())
                .limit(10)
                .fetch();
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
                return matePost.maxParticipantsCenters.gt(matePost.currentParticipantsCenters);
            } else if(position.equals(PositionType.GUARD)){
                return matePost.maxParticipantsGuards.gt(matePost.currentParticipantsGuards);
            } else if(position.equals(PositionType.FORWARD)){
                return matePost.maxParticipantsForwards.gt(matePost.currentParticipantsForwards);
            } else if(position.equals(PositionType.UNSPECIFIED)){
                return matePost.maxParticipantsOthers.gt(matePost.currentParticipantsOthers);
            }else {
                return null;
            }
        } else {
            return null;
        }
    }

    private BooleanExpression ltCreatedAt(LocalDateTime cursorTime) {
        if(cursorTime == null){
            return null;
        }
        return matePost.createdAt.lt(cursorTime);
    }

}
