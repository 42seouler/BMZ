package com.hotechcourse.oauth.service;

import com.hotechcourse.oauth.config.AppProperties;
import com.hotechcourse.oauth.exception.TokenRefreshException;
import com.hotechcourse.oauth.model.Member;
import com.hotechcourse.oauth.model.RefreshToken;
import com.hotechcourse.oauth.repository.MemberRepository;
import com.hotechcourse.oauth.repository.RefreshTokenRepository;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenService {

    private final AppProperties appProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow();

        return null;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(new Date()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new login request");
        }
        return token;
    }
}
