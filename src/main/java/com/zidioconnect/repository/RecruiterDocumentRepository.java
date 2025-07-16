package com.zidioconnect.repository;

import com.zidioconnect.model.RecruiterDocument;
import com.zidioconnect.model.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruiterDocumentRepository extends JpaRepository<RecruiterDocument, Long> {
    List<RecruiterDocument> findByRecruiter(Recruiter recruiter);

    List<RecruiterDocument> findByRecruiterAndType(Recruiter recruiter, String type);

    List<RecruiterDocument> findByRecruiterAndTypeAndStatus(Recruiter recruiter, String type, String status);
}