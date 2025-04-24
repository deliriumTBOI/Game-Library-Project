package com.gamelib.gamelib;

import com.gamelib.gamelib.service.impl.LogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    @InjectMocks
    private LogServiceImpl logService;

    @BeforeEach
    void setUp() throws Exception {
        // Создаем тестовую директорию и файл логов
        Files.createDirectories(Paths.get("logs"));
        String testDate = "2023-01-01";
        Files.write(Paths.get("logs/gamelib.log"),
                (testDate + " Test log entry 1\n" +
                        testDate + " Test log entry 2\n" +
                        "2023-01-02 Other date log entry\n").getBytes());
    }

    @Test
    void getTaskStatus_WhenTaskNotFound_ShouldReturnNotFound() {
        String nonExistentTaskId = UUID.randomUUID().toString();

        String status = logService.getTaskStatus(nonExistentTaskId);

        assertEquals("NOT_FOUND", status);
    }

    @Test
    void getLogFilePath_WhenTaskNotFound_ShouldReturnNull() {
        // Arrange
        String nonExistentTaskId = UUID.randomUUID().toString();

        // Act
        String filePath = logService.getLogFilePath(nonExistentTaskId);

        // Assert
        assertNull(filePath);
    }
}