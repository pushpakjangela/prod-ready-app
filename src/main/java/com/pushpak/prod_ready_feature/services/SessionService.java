package com.pushpak.prod_ready_feature.services;

import com.pushpak.prod_ready_feature.entities.SessionEntity;
import com.pushpak.prod_ready_feature.entities.User;
import com.pushpak.prod_ready_feature.exception.SessionNotFoundException;
import com.pushpak.prod_ready_feature.repositories.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionService {
    private final SessionRepository sessionRepository;
    private final int SESSION_LIMIT = 2;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void generateNewSession(User user,String refreshToken){
        List<SessionEntity> userSessions = sessionRepository.findByUser(user);

        if(userSessions.size()==2){
            userSessions.sort((Comparator.comparing(SessionEntity::getLastUsedAt)));
            SessionEntity leastUsedSession = userSessions.getFirst();
            sessionRepository.delete(leastUsedSession);
        }
        SessionEntity newSession = SessionEntity.builder()
                .refreshToken(refreshToken)
                .lastUsedAt(LocalDateTime.now())
                .user(user)
                .build();
        sessionRepository.save(newSession);
    }

    public void validateSession(String refreshToken){
        SessionEntity sessions = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(()->new SessionNotFoundException("Session not found for this refresh token"));

        logger.info("session last used at " + sessions.getLastUsedAt());

        sessions.setLastUsedAt(LocalDateTime.now());

        logger.info("session updated "+sessions.getLastUsedAt());
        sessionRepository.save(sessions);
    }
}
