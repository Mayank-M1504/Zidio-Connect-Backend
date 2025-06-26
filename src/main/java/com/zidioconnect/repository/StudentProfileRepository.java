package com.zidioconnect.repository;

import com.zidioconnect.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
    Optional<StudentProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}