package sync.slamtalk.mate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import sync.slamtalk.mate.service.RegisterMatePostService;

@RestController("/api/mate")
@RequiredArgsConstructor
public class RegisterMatePostController {

    RegisterMatePostService registerMatePostService;



}
