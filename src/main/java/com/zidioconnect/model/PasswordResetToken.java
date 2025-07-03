package com.zidioconnect.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = Student.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "student_id")
    private Student student;

    private LocalDateTime expiryDate;

    private boolean used = false;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}