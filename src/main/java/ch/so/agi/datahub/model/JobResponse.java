package ch.so.agi.datahub.model;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobResponse(
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String status,
        Integer queuePosition,
        String operat,
        String theme, 
        String organisation,
        String message,
        String validationStatus,
        String logFileLocation,
        String xtfLogFileLocation,
        String csvLogFileLocation
        ) {}
