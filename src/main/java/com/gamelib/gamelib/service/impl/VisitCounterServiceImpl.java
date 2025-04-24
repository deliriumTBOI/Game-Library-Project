package com.gamelib.gamelib.service.impl;

import com.gamelib.gamelib.service.VisitCounterService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class VisitCounterServiceImpl implements VisitCounterService {
    private final Map<String, Integer> visitCounts = new ConcurrentHashMap<>();

    @Override
    public void incrementVisitCount(String url) {
        visitCounts.compute(url, (key, value) -> value == null ? 1 : value + 1);
    }

    @Override
    public int getVisitCount(String url) {
        return visitCounts.getOrDefault(url, 0);
    }

    @Override
    public int getTotalVisitCount() {
        return visitCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    @Override
    public Map<String, Integer> getAllVisitCounts() {
        return new ConcurrentHashMap<>(visitCounts);
    }
}