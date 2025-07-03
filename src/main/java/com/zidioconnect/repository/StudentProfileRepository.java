package com.zidioconnect.repository;

import com.zidioconnect.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByStudentId(Long studentId);

    Optional<StudentProfile> findByEmail(String email);
}