package net.dynu.wpeckers.walktraveler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

    private static long lastShownSessions;

    private static final Map<String,Long> userIdToLastCollectTimeMap = new HashMap<>();
    private static final Map<String,Integer> userIdToCollectCountMap = new HashMap<>();

    public int getUserCollectCount(String email) {
        Integer collectCount = userIdToCollectCountMap.get(email);
        return collectCount == null ? 0 : collectCount.intValue();
    }

    public long getUserLastCollectTime(String email) {
        Long lastCollectTime = userIdToLastCollectTimeMap.get(email);
        return lastCollectTime == null ? 0 : lastCollectTime;
    }

    public void addUserCollectCount(String email) {
        int newCount = getUserCollectCount(email) + 1;
        userIdToCollectCountMap.put(email, newCount);
        userIdToLastCollectTimeMap.put(email, System.currentTimeMillis());
    }

    public void resetUserLastCollectData(String email) {
        this.userIdToLastCollectTimeMap.put(email, 0L);
        this.userIdToCollectCountMap.put(email, 0);
    }

    public static long getNextShownSessionTime() {
        return lastShownSessions;
    }

    public static void setNextShowSessionTime(long time) {
        lastShownSessions = time;
    }
}
