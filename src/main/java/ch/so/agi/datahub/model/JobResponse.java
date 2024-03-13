package ch.so.agi.datahub.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobResponse(
        String jobId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String status,
        Long queuePosition,
        String operat,
        String theme, 
        String organisation,
        String message,
        String validationStatus,
        String logFileLocation,
        String xtfLogFileLocation,
        String csvLogFileLocation
        ) {}
