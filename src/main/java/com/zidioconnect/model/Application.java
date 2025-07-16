package com.zidioconnect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_profile_id", nullable = false)
    private StudentProfile studentProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private RecruiterJob job;

    // Document references (resume, marksheet, certificates)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private StudentDocument resume;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marksheet_id")
    private StudentDocument marksheet;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "application_certificates", joinColumns = @JoinColumn(name = "application_id"), inverseJoinColumns = @JoinColumn(name = "certificate_id"))
    private List<StudentCertificate> certificates;

    private String status = "APPLIED"; // APPLIED, REVIEWED, ACCEPTED, REJECTED, etc.

    private LocalDateTime appliedAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT", nullable = true)
    private String answerForRecruiter;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public void setStudentProfile(StudentProfile studentProfile) {
        this.studentProfile = studentProfile;
    }

    public RecruiterJob getJob() {
        return job;
    }

    public void setJob(RecruiterJob job) {
        this.job = job;
    }

    public StudentDocument getResume() {
        return resume;
    }

    public void setResume(StudentDocument resume) {
        this.resume = resume;
    }

    public StudentDocument getMarksheet() {
        return marksheet;
    }

    public void setMarksheet(StudentDocument marksheet) {
        this.marksheet = marksheet;
    }

    public List<StudentCertificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<StudentCertificate> certificates) {
        this.certificates = certificates;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public String getAnswerForRecruiter() {
        return answerForRecruiter;
    }

    public void setAnswerForRecruiter(String answerForRecruiter) {
        this.answerForRecruiter = answerForRecruiter;
    }
}