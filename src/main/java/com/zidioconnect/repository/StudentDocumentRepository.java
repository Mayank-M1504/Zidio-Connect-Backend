package com.zidioconnect.repository;

import com.zidioconnect.model.StudentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentDocumentRepository extends JpaRepository<StudentDocument, Long> {
    List<StudentDocument> findByProfileId(Long profileId);

    List<StudentDocument> findByProfileIdAndType(Long profileId, String type);

    Optional<StudentDocument> findByProfileIdAndTypeAndStatus(Long profileId, String type, String status);

    List<StudentDocument> findByProfileIdAndStatus(Long profileId, String status);
}