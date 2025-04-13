package com.gamelib.gamelib.service;

import java.time.LocalDate;
import org.springframework.core.io.Resource;

public interface LogService {
    Resource getLogFileByDate(LocalDate date);
}