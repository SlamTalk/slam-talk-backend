package sync.slamtalk.mate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sync.slamtalk.mate.entity.MatePost;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.mate.service.MatePostService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class RegisterMatePostControllerTest {

    @Autowired
    MatePostService MatePostService;
    @Autowired
    MatePostRepository matePostRepository;

    List<MatePost> matePostList;
//
//    @BeforeEach
//    void beforeTest() {
//        matePostList = new ArrayList<>();
//        for(int i = 0; i < 10; i++){
//            MatePost temp = new MatePost();
//            temp.setUserId(i);
//            temp.setUserNickname("테스트");
//            temp.setUserLocation("서울");
//            temp.setTitle("테스트 게시글");
//            temp.setScheduledTime("2024-03-15 15:30");
//            temp.setContent("테스트 게시글 내용");
//            temp.setMaxParticipantsCenters(1);
//            temp.setMaxParticipantsGuards(1);
//            temp.setMaxParticipantsForwards(1);
//            temp.setMaxParticipantsOthers(1);
//            temp.setRecruitmentStatus("모집중");
//            temp.setSoftDelete(false);
//            temp.setMaxParticipants(4);
//            temp.setSkillLevel("초보");
//            temp.setLocationDetail("서울");
//            matePostList.add(temp);
//        }
//    }

    @Test
    void registerMatePost() {
        // * 테스트를 위해 MatePost를 저장한다.
        for(MatePost matePost : matePostList){
            MatePostService.registerMatePost(matePost);
        }

        // * 저장된 MatePost를 가져온다.
        List<MatePost> result = matePostRepository.findAll();

        // * 저장된 MatePost의 개수가 10개인지 확인한다.
        assertEquals(10, result.size());
    }

    long[] matePostId = new long[10];
    int count = 0;
    @Test
    void matchBeforeAndAfter(){
        for(MatePost matePost : matePostList){
            matePostId[count] = MatePostService.registerMatePost(matePost);
            count++;
        }

        for(int i = 0; i < 10; i++){
            MatePost result = matePostRepository.findById(matePostId[i]).get();

            Assertions.assertEquals(matePostList.get(i).getUserId(), result.getUserId());

        }
    }

    @AfterEach
    void deleteMatePost() {
        // * 저장된 MatePost를 삭제한다.
        matePostRepository.deleteAll();
    }
}