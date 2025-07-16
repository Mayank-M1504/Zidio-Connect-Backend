package com.zidioconnect.service;

import com.zidioconnect.model.*;
import com.zidioconnect.repository.*;
import com.zidioconnect.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    @Autowired
    private RecruiterJobRepository recruiterJobRepository;
    @Autowired
    private StudentDocumentRepository studentDocumentRepository;
    @Autowired
    private StudentCertificateRepository studentCertificateRepository;

    public Application apply(ApplicationRequest request, Long studentProfileId) {
        StudentProfile profile = studentProfileRepository.findById(studentProfileId).orElseThrow();
        RecruiterJob job = recruiterJobRepository.findById(request.jobId).orElseThrow();
        StudentDocument resume = studentDocumentRepository.findById(request.resumeId).orElse(null);
        StudentDocument marksheet = studentDocumentRepository.findById(request.marksheetId).orElse(null);
        List<StudentCertificate> certificates = request.certificateIds == null ? List.of()
                : request.certificateIds.stream().map(id -> studentCertificateRepository.findById(id).orElse(null))
                        .collect(Collectors.toList());

        Application application = new Application();
        application.setStudentProfile(profile);
        application.setJob(job);
        application.setResume(resume);
        application.setMarksheet(marksheet);
        application.setCertificates(certificates);
        application.setStatus("APPLIED");
        application.setAnswerForRecruiter(request.answerForRecruiter);
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByStudent(Long studentProfileId) {
        StudentProfile profile = studentProfileRepository.findById(studentProfileId).orElseThrow();
        return applicationRepository.findByStudentProfile(profile);
    }

    public List<Application> getApplicationsByJob(Long jobId) {
        RecruiterJob job = recruiterJobRepository.findById(jobId).orElseThrow();
        return applicationRepository.findByJob(job);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public Application save(Application app) {
        return applicationRepository.save(app);
    }
}