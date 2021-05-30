package com.hotechcourse.oauth.controller;

import com.hotechcourse.oauth.exception.BadRequestException;
import com.hotechcourse.oauth.exception.TokenRefreshException;
import com.hotechcourse.oauth.model.AuthProvider;
import com.hotechcourse.oauth.model.User;
import com.hotechcourse.oauth.model.RefreshToken;
import com.hotechcourse.oauth.payload.*;
import com.hotechcourse.oauth.repository.MemberRepository;
import com.hotechcourse.oauth.security.TokenProvider;
import java.net.URI;
import javax.validation.Valid;

import com.hotechcourse.oauth.service.RefreshTokenService;
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
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(memberRepository.existsByEmail(signUpRequest.getEmail())) {
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
                .body(new ApiResponse(true, "User registered successfully@"));
    }

    @PostMapping("/refreshtoken")
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
