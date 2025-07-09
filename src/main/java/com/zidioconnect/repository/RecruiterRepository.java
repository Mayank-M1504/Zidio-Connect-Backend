package com.zidioconnect.repository;

import com.zidioconnect.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<Recruiter, Long> {
    Optional<Recruiter> findByEmail(String email);

    boolean existsByEmail(String email);
}