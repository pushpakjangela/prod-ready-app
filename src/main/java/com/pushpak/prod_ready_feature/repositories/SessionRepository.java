package com.pushpak.prod_ready_feature.repositories;

import com.pushpak.prod_ready_feature.entities.SessionEntity;
import com.pushpak.prod_ready_feature.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
    List<SessionEntity> findByUser(User user);

    Optional<SessionEntity> findByRefreshToken(String refreshToken);
}
