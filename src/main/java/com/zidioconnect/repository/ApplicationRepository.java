package com.zidioconnect.repository;

import com.zidioconnect.model.Application;
import com.zidioconnect.model.StudentProfile;
import com.zidioconnect.model.RecruiterJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentProfile(StudentProfile studentProfile);

    List<Application> findByJob(RecruiterJob job);
}