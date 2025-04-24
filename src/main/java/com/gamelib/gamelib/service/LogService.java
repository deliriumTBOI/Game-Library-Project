package com.gamelib.gamelib.service;

import java.util.concurrent.CompletableFuture;

public interface LogService {
    CompletableFuture<String> generateLogFileForDateAsync(String date);

    String getLogFilePath(String taskId);

    String getTaskStatus(String taskId);
}