package com.hotechcourse.oauth.security;

import com.hotechcourse.oauth.config.AppProperties;
import com.hotechcourse.oauth.model.RefreshToken;
import com.hotechcourse.oauth.repository.MemberRepository;
import com.hotechcourse.oauth.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    private final AppProperties appProperties;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec());

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret())))
                .compact();
    }

    @Transactional
    public String createRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Long memberId = userPrincipal.getId();
        Date expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec() * 2);

        String refreshToken = Jwts.builder()
            .setSubject(Long.toString(memberId))
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret())))
            .compact();

        refreshTokenRepository.save(RefreshToken.builder()
            .member(memberRepository.findById(memberId).orElseThrow())
            .token(refreshToken)
            .expiryDate(expiryDate)
            .build()
        );

       return refreshToken;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret())))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(appProperties.getAuth().getTokenSecret()))).build();
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

}
