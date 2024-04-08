package ch.so.agi.datahub.model;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {
    String jobId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String status;
    Long queuePosition;
    String operat;
    String theme;
    String organisation;
    String message;
    String validationStatus;
    String logFileLocation;
    String xtfLogFileLocation;
    String csvLogFileLocation;
    
    public JobResponse(String jobId, LocalDateTime createdAt, LocalDateTime updatedAt, String status,
            Long queuePosition, String operat, String theme, String organisation, String message,
            String validationStatus, String logFileLocation, String xtfLogFileLocation, String csvLogFileLocation) {
        this.jobId = jobId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.queuePosition = queuePosition;
        this.operat = operat;
        this.theme = theme;
        this.organisation = organisation;
        this.message = message;
        this.validationStatus = validationStatus;
        this.logFileLocation = logFileLocation;
        this.xtfLogFileLocation = xtfLogFileLocation;
        this.csvLogFileLocation = csvLogFileLocation;
    }
    
    public String getJobId() {
        return jobId;
    }
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Long getQueuePosition() {
        return queuePosition;
    }
    public void setQueuePosition(Long queuePosition) {
        this.queuePosition = queuePosition;
    }
    public String getOperat() {
        return operat;
    }
    public void setOperat(String operat) {
        this.operat = operat;
    }
    public String getTheme() {
        return theme;
    }
    public void setTheme(String theme) {
        this.theme = theme;
    }
    public String getOrganisation() {
        return organisation;
    }
    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getValidationStatus() {
        return validationStatus;
    }
    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }
    public String getLogFileLocation() {
        return logFileLocation;
    }
    public void setLogFileLocation(String logFileLocation) {
        this.logFileLocation = logFileLocation;
    }
    public String getXtfLogFileLocation() {
        return xtfLogFileLocation;
    }
    public void setXtfLogFileLocation(String xtfLogFileLocation) {
        this.xtfLogFileLocation = xtfLogFileLocation;
    }
    public String getCsvLogFileLocation() {
        return csvLogFileLocation;
    }
    public void setCsvLogFileLocation(String csvLogFileLocation) {
        this.csvLogFileLocation = csvLogFileLocation;
    }
}
