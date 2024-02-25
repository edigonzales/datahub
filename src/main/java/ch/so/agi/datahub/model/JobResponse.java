package ch.so.agi.datahub.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobResponse(
        String createdAt,
        String updatedAt,
        String status,
        Integer queuePosition,
        String message,
        String validationStatus,
        String logFileLocation,
        String xtfLogFileLocation,
        String csvLogFileLocation
        ) {}
