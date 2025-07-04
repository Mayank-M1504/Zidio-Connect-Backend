package com.zidioconnect.repository;

import com.zidioconnect.model.StudentCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentCertificateRepository extends JpaRepository<StudentCertificate, Long> {
    List<StudentCertificate> findByProfileId(Long profileId);

    List<StudentCertificate> findByProfileIdAndStatus(Long profileId, String status);
}