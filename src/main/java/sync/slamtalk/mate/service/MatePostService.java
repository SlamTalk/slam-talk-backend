package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.mate.domain.MatePost;
import sync.slamtalk.mate.repository.MatePostRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MatePostService {

    private final MatePostRepository matePostRepository;

    public long registerMatePost(MatePost matePost){
        MatePost result = matePostRepository.save(matePost);
        return result.getMatePostId(); // * 저장된 게시글의 아이디를 반환한다.
    }
}
