package com.gamelib.gamelib.service.impl;

import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.service.LogService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Value("${logging.file.name}")
    private String logFileName;

    @Override
    public Resource getLogFileByDate(LocalDate date) {
        String datePattern = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String dateRegex = "^" + datePattern + ".*";
        Pattern pattern = Pattern.compile(dateRegex);

        File tempFile = null;

        try {
            tempFile = File.createTempFile("gamelib-" + datePattern, ".log");
            BufferedReader reader = new BufferedReader(new FileReader(logFileName));
            FileWriter writer = new FileWriter(tempFile);

            String line;
            boolean hasLogs = false;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    writer.write(line + System.lineSeparator());
                    hasLogs = true;
                }
            }

            reader.close();
            writer.close();

            if (!hasLogs) {
                Files.delete(tempFile.toPath());
                throw new ResourceNotFoundException("No logs found for date: " + datePattern);
            }

            return new FileSystemResource(tempFile);
        } catch (IOException e) {
            if (tempFile != null && tempFile.exists()) {
                try {
                    Files.delete(tempFile.toPath());
                } catch (IOException ex) {
                    // Ignore exception during cleanup
                }
            }
            throw new RuntimeException("Error processing log file: " + e.getMessage(), e);
        }
    }
}