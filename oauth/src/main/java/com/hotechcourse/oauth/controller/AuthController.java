package com.hotechcourse.oauth.controller;

import com.hotechcourse.oauth.exception.BadRequestException;
import com.hotechcourse.oauth.exception.TokenRefreshException;
import com.hotechcourse.oauth.model.AuthProvider;
import com.hotechcourse.oauth.model.User;
import com.hotechcourse.oauth.model.RefreshToken;
import com.hotechcourse.oauth.payload.*;
import com.hotechcourse.oauth.payload.ApiResponse;
import com.hotechcourse.oauth.repository.MemberRepository;
import com.hotechcourse.oauth.security.TokenProvider;

import java.net.URI;
import java.util.Optional;
import javax.validation.Valid;

import com.hotechcourse.oauth.service.RefreshTokenService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    @ApiOperation(value = "email, 비밀번호로 로그인", notes = "성공시 access, refresh token을 body에 넣어서 반환합니다.")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long memberId = memberRepository.findByEmail(loginRequest.getEmail()).orElseThrow().getId();
        String accessToken = tokenProvider.generateJwtToken(authentication);
        refreshTokenService.deleteByUserId(memberId);
        String refreshToken = tokenProvider.createRefreshToken(Long.toString(memberId));
        return ResponseEntity.ok(AuthResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    @PostMapping("/signup")
    @ApiOperation(value = "이름, 이메일, 비밀번호로 회원 가입", notes = "성공시 201 \"User registered successfully!\" 메시지 반환합니다.")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (memberRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("Email address already in use.");
        }

        // Creating user's account
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setProvider(AuthProvider.local);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User result = memberRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    @ApiOperation(value = "access token 만료시 refresh token으로 토큰 갱신하기", notes = "성공시 access, refresh token을 body에 넣어서 반환합니다.")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {

        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(member -> {
                    Long memberId = member.getId();
                    String accessToken = tokenProvider.createAccessToken(Long.toString(memberId));
                    refreshTokenService.deleteByUserId(memberId);
                    String refreshToken = tokenProvider.createRefreshToken(Long.toString(memberId));
                    return ResponseEntity.ok(TokenRefreshResponse
                            .builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build());
                }).orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

}
