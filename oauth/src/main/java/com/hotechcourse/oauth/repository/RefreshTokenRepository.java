package com.hotechcourse.oauth.repository;

import com.hotechcourse.oauth.model.User;
import com.hotechcourse.oauth.model.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
