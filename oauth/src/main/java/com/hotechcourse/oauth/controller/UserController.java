package com.hotechcourse.oauth.controller;

import com.hotechcourse.oauth.exception.ResourceNotFoundException;
import com.hotechcourse.oauth.model.Member;
import com.hotechcourse.oauth.repository.MemberRepository;
import com.hotechcourse.oauth.security.CurrentUser;
import com.hotechcourse.oauth.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public Member getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return memberRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
