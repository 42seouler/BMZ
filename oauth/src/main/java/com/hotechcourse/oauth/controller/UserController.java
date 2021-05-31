package com.hotechcourse.oauth.controller;

import com.hotechcourse.oauth.exception.ResourceNotFoundException;
import com.hotechcourse.oauth.model.User;
import com.hotechcourse.oauth.repository.MemberRepository;
import com.hotechcourse.oauth.security.CurrentUser;
import com.hotechcourse.oauth.security.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final MemberRepository memberRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "Authorize 이후 Get 요청시 유저 정보를 반환합니다.", notes = "Swagger에서 Authorize 할 경우 \"Bearer JWT\" 양식으로 요청 보내기")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return memberRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
