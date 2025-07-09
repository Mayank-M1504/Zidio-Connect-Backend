package com.zidioconnect.repository;

import com.zidioconnect.model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
    Optional<RecruiterProfile> findByEmail(String email);
}