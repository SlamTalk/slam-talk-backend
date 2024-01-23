package sync.slamtalk.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.entity.SocialType;

@Slf4j
@RequiredArgsConstructor
@Component("userDetailsService")
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {
   private final UserRepository userRepository;

   /**
    * 유저 이메일과 소셜타입
    * */
   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      log.debug("유저 정보 = {}", username);

       // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 return
      return userRepository.findByEmailAndSocialType(username, SocialType.LOCAL)
              .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
   }
}
