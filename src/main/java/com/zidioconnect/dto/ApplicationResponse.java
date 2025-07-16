package com.zidioconnect.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationResponse {
    public Long id;
    public Long jobId;
    public String jobTitle;
    public Long studentProfileId;
    public String studentName;
    public String studentEmail;
    public String phone;
    public String college;
    public String course;
    public String yearOfStudy;
    public DocumentInfo resume;
    public DocumentInfo marksheet;
    public List<DocumentInfo> certificates;
    public String status;
    public LocalDateTime appliedAt;
    public String questionForApplicant;
    public String answerForRecruiter;

    public static class DocumentInfo {
        public Long id;
        public String name;
        public String url;
        public String status;
    }
}