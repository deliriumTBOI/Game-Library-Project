package com.gamelib.gamelib.service;

import java.util.Map;

public interface VisitCounterService {
    void incrementVisitCount(String url);

    int getVisitCount(String url);

    int getTotalVisitCount();

    Map<String, Integer> getAllVisitCounts();
}