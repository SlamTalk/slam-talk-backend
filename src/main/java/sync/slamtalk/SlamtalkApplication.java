package sync.slamtalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;


//@SpringBootApplication
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SlamtalkApplication {
	public static void main(String[] args) {
		SpringApplication.run(SlamtalkApplication.class, args);
	}

}


/*
	Spring Security 의존성을 추가했지만,
	아직 인증단계를 개발하지 않은 경우 Application에서 exclude를 이용해 Security 기능을 꺼둘 수 있다.
*/