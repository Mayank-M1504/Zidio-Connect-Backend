package com.zidioconnect.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "student_document")
public class StudentDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private StudentProfile profile;

    @Column(nullable = false)
    private String type; // e.g. resume, profile_picture, marksheet, identity_proof, certificate

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt = new Date();

    private String certificateName; // only for certificates

    // Getters and setters omitted for brevity
}