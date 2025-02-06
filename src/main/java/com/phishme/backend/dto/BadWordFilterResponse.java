package com.phishme.backend.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class BadWordFilterResponse {
    @Getter
    public static class BadWordFilterStatus {
        public long code;
        public String message;
        public String description;
    }

    @Getter
    public static class BadWordFilterDetected {
        public long length;
        public String filteredWord;
    }

    public String trackingId;
    public BadWordFilterStatus status;
    public List<BadWordFilterDetected> detected;
    public String filtered;
    public String elapsed;
}
