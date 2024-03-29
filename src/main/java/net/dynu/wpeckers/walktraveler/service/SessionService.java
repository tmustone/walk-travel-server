package net.dynu.wpeckers.walktraveler.service;

import lombok.extern.slf4j.Slf4j;
import net.dynu.wpeckers.walktraveler.database.model.UserEntity;
import net.dynu.wpeckers.walktraveler.exceptions.SessionTimeoutException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SessionService {

    private static final Map<String, UserEntity> sessionIdToUserMap = new HashMap<>();
    private static final Map<String, Long> sessionIdToExpirationTimeMap = new HashMap<>();
    private static final int SESSION_TIMEOUT_SECONDS = 60;

    public UserEntity getUser(String sessionId) throws SessionTimeoutException {
        UserEntity user = sessionIdToUserMap.get(sessionId);
        if (user != null) {
            sessionIdToExpirationTimeMap.put(sessionId, System.currentTimeMillis());
            return user;
        } else {
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

    public List<UserEntity> getLoggedInUsers() {
        List<UserEntity> onlineUsers = new LinkedList<>();
        long now = System.currentTimeMillis();
        long timeout = SESSION_TIMEOUT_SECONDS*1000;
        synchronized (sessionIdToUserMap) {
            List<String> removeSessions = new LinkedList<>();
            for (String sessionId : sessionIdToUserMap.keySet()) {
                Long expirationTime = sessionIdToExpirationTimeMap.get(sessionId);
                UserEntity user = sessionIdToUserMap.get(sessionId);
                if (expirationTime == null || expirationTime.longValue() + timeout < now) {
                    log.info("Session timeout {} VS {} for session {} with user {}", expirationTime.longValue() + timeout, now, sessionId, user != null ? user.getEmail() : null);
                    removeSessions.add(sessionId);
                } else {
                    onlineUsers.add(user);
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

    public UserEntity login(String sessionId, UserEntity user) {
        if (sessionId != null) {
            log.info("User is logged in with session {} : {}" , sessionId, user);
            sessionIdToUserMap.put(sessionId, user);
            sessionIdToExpirationTimeMap.put(sessionId, new Long(System.currentTimeMillis()));
            return user;
        } else {
            throw new RuntimeException("Cannot create session when session ID is null!");
        }
    }
}
