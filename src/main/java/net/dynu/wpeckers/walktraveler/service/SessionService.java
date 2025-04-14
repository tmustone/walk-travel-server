package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private static final Map<String, UserEntity> sessionIdToUserMap = new HashMap<>();
    private static final Map<String, Long> sessionIdToExpirationTimeMap = new HashMap<>();
    private static final int SESSION_TIMEOUT_SECONDS = 60;

    private final UserService userService;

    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    public Long getSessionExpirationTime(String sessionId) {
        return sessionIdToExpirationTimeMap.get(sessionId);
    }

    public UserEntity getUser(String sessionId) throws SessionTimeoutException {

        UserEntity user = sessionIdToUserMap.get(sessionId);
        if (user != null) {
            sessionIdToExpirationTimeMap.put(sessionId, System.currentTimeMillis());
            return user;
        } else {
            // For testing purposes in LOCAL and DEFAULT profile, sessionId "test" is working!
            if ("test".equals(sessionId) && (activeProfiles.contains("default") || activeProfiles.contains("local"))) {
                String tempUser = "tommi_mustonen@hotmail.com";
                log.warn("Session ID test is now activated with spring profile {} for user {}", activeProfiles, tempUser);
                return this.login("test",userService.readByEmail(tempUser));
            }
            log.error("User not found from session with session ID \"{}\". Cannot continue : {}", sessionId, sessionIdToUserMap.keySet());
            throw new SessionTimeoutException("Session not found with ID " + sessionId + "!");
        }
    }

    public UserEntity validateSession(String sessionId) throws SessionTimeoutException {
        if (sessionId == null) {
            throw new RuntimeException("Session ID is NULL for user!");
        }
        return this.getUser(sessionId);
    }

    public Map<String, UserEntity> getLoggedInUsers() {
        Map<String, UserEntity> onlineUsers = new LinkedHashMap<String, UserEntity>();
        long now = System.currentTimeMillis();
        long timeout = SESSION_TIMEOUT_SECONDS*1000;
        synchronized (sessionIdToUserMap) {
            List<String> removeSessions = new LinkedList<>();
            for (String sessionId : sessionIdToUserMap.keySet()) {
                Long expirationTime = sessionIdToExpirationTimeMap.get(sessionId);
                UserEntity user = sessionIdToUserMap.get(sessionId);
                if (expirationTime == null || expirationTime + timeout < now) {
                    log.info("Session timeout {} VS {} for session {} with user {}", expirationTime != null ? (expirationTime + timeout) : -1, now, sessionId, user != null ? user.getEmail() : null);
                    removeSessions.add(sessionId);
                } else {
                    onlineUsers.put(sessionId, user);
                }
            }
            for (String sessionIdToRemove : removeSessions) {
                UserEntity removedUser = sessionIdToUserMap.remove(sessionIdToRemove);
                sessionIdToExpirationTimeMap.remove(sessionIdToRemove);
                log.info("Removed {} with session {} from sessions. Session expired!", removedUser.getEmail(), sessionIdToRemove);
            }
        }
        return onlineUsers;
    }

    public void updateUser(String sessionId, UserEntity user) {
        synchronized (sessionIdToUserMap) {
            sessionIdToUserMap.put(sessionId, user);
        }
    }

    public UserEntity login(String sessionId, UserEntity user) {
        if (sessionId != null) {
            log.info("User is logged in with session {} : {}" , sessionId, user);
            synchronized (sessionIdToUserMap) {
                sessionIdToUserMap.put(sessionId, user);
                sessionIdToExpirationTimeMap.put(sessionId, System.currentTimeMillis());
            }
            return user;
        } else {
            throw new RuntimeException("Cannot create session when session ID is null!");
        }
    }

    public void logout(String sessionId, UserEntity user) {
        log.info("Logging out user {} with session {}" , user != null ? user.getEmail() : null, sessionId);
        synchronized (sessionIdToUserMap) {
            sessionIdToUserMap.remove(sessionId, user);
            sessionIdToExpirationTimeMap.remove(sessionId);
        }
    }
}
