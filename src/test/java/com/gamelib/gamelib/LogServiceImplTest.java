package com.gamelib.gamelib;

import com.gamelib.gamelib.exception.LogProcessingException;
import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.service.impl.LogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceImplTest {

    @TempDir
    Path tempDir;

    @InjectMocks
    @Spy
    private LogServiceImpl logService;

    @BeforeEach
    void setUp() throws IOException {
        // Create a test log file with sample content
        File testLogFile = new File(tempDir.toFile(), "test.log");

        try (FileWriter writer = new FileWriter(testLogFile)) {
            writer.write("2023-05-15 10:30:45 INFO  Sample log entry 1\n");
            writer.write("2023-05-15 11:22:33 ERROR Sample error log\n");
            writer.write("2023-05-16 09:11:22 INFO  Sample log entry 2\n");
            writer.write("2023-05-17 13:45:30 WARN  Sample warning log\n");
        }

        // Set the log file name in the service
        ReflectionTestUtils.setField(logService, "logFileName", testLogFile.getAbsolutePath());
    }

    @Test
    void getLogFileByDate_ShouldReturnFileWithFilteredLogs_WhenLogsExistForDate() throws IOException {
        // Arrange
        LocalDate date = LocalDate.of(2023, 5, 15);

        // Act
        Resource result = logService.getLogFileByDate(date);

        // Assert
        assertTrue(result.exists());
        assertTrue(result.isReadable());

        String content = new String(result.getInputStream().readAllBytes());
        assertTrue(content.contains("2023-05-15 10:30:45"));
        assertTrue(content.contains("2023-05-15 11:22:33"));
        assertFalse(content.contains("2023-05-16"));
        assertFalse(content.contains("2023-05-17"));
    }

    @Test
    void getLogFileByDate_ShouldThrowResourceNotFoundException_WhenNoLogsForDate() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 5, 20); // Date with no logs

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> logService.getLogFileByDate(date));

        assertEquals("No logs found for date: 2023-05-20", exception.getMessage());
    }

    @Test
    void getLogFileByDate_ShouldThrowLogProcessingException_WhenIOExceptionOccurs() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 5, 15);

        // Если logService уже является моком
        when(logService.getLogFileByDate(date))
                .thenThrow(new LogProcessingException("Error processing log file", new IOException("Test IO Exception")));

        // Act & Assert
        assertThrows(LogProcessingException.class, () -> logService.getLogFileByDate(date));
    }

    @Test
    void getLogFileByDate_ShouldHandleFileReadErrors() {
        // Arrange
        LocalDate date = LocalDate.of(2023, 5, 15);

        // Set an invalid log file path
        ReflectionTestUtils.setField(logService, "logFileName", "non_existent_file.log");

        // Act & Assert
        LogProcessingException exception = assertThrows(LogProcessingException.class,
                () -> logService.getLogFileByDate(date));

        assertTrue(exception.getMessage().contains("Error processing log file"));
    }
}