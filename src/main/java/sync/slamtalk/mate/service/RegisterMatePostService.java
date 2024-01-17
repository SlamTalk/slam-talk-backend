package sync.slamtalk.mate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sync.slamtalk.mate.repository.MatePostRepository;

@Service
@RequiredArgsConstructor
public class RegisterMatePostService {

    private final MatePostRepository matePostRepository;


}
