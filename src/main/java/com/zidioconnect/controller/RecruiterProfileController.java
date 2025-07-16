package com.zidioconnect.controller;

import com.zidioconnect.dto.RecruiterProfileRequest;
import com.zidioconnect.dto.RecruiterProfileResponse;
import com.zidioconnect.service.RecruiterProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.zidioconnect.model.RecruiterDocument;
import com.zidioconnect.model.Recruiter;
import com.zidioconnect.repository.RecruiterDocumentRepository;
import com.zidioconnect.repository.RecruiterRepository;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;
import com.zidioconnect.service.FileUploadService;

@RestController
@RequestMapping("/api/recruiter/profile")
public class RecruiterProfileController {
    @Autowired
    private RecruiterProfileService profileService;
    @Autowired
    private RecruiterDocumentRepository recruiterDocumentRepository;
    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<RecruiterProfileResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        RecruiterProfileResponse resp = profileService.getProfileByEmail(email);
        if (resp == null)
            return ResponseEntity.notFound().build();
        // Ensure email is set in the response
        resp.setEmail(email);
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<RecruiterProfileResponse> createOrUpdateProfile(Authentication authentication,
            @RequestBody RecruiterProfileRequest req) {
        String email = authentication.getName();
        req.email = email; // Ensure email is set from auth
        RecruiterProfileResponse resp = profileService.createOrUpdateProfile(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<?> uploadRecruiterDocument(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Recruiter not found"));
        }
        try {
            String url = fileUploadService.uploadDocument(file, type);
            RecruiterDocument doc = new RecruiterDocument();
            doc.setRecruiter(recruiter);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileSize(file.getSize());
            doc.setContentType(file.getContentType());
            doc.setUrl(url);
            doc.setStatus("PENDING");
            doc.setUploadedAt(new java.util.Date());
            doc.setType(type);
            recruiterDocumentRepository.save(doc);
            return ResponseEntity.ok(java.util.Map.of("message", "Document uploaded successfully", "document", doc));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/upload-logo")
    public ResponseEntity<?> uploadCompanyLogo(Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Recruiter not found"));
        }
        try {
            String url = fileUploadService.uploadProfilePicture(file); // Reuse profile picture upload logic
            recruiter.setCompanyLogo(url);
            recruiterRepository.save(recruiter);
            return ResponseEntity.ok(java.util.Map.of("companyLogoUrl", url));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload company logo: " + e.getMessage());
        }
    }

    @GetMapping("/documents")
    public ResponseEntity<?> getRecruiterDocuments(Authentication authentication) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Recruiter not found"));
        }
        var docs = recruiterDocumentRepository.findByRecruiter(recruiter);
        return ResponseEntity.ok(docs);
    }

    @GetMapping("/document/{id}")
    public ResponseEntity<?> getRecruiterDocumentById(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Recruiter not found"));
        }
        var docOpt = recruiterDocumentRepository.findById(id);
        if (docOpt.isEmpty() || !docOpt.get().getRecruiter().getId().equals(recruiter.getId())) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", "Document not found"));
        }
        return ResponseEntity.ok(docOpt.get());
    }

    @DeleteMapping("/document/{id}")
    public ResponseEntity<?> deleteRecruiterDocument(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Recruiter recruiter = recruiterRepository.findByEmail(email).orElse(null);
        if (recruiter == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Recruiter not found"));
        }
        var docOpt = recruiterDocumentRepository.findById(id);
        if (docOpt.isEmpty() || !docOpt.get().getRecruiter().getId().equals(recruiter.getId())) {
            return ResponseEntity.status(404).body(java.util.Map.of("error", "Document not found"));
        }
        recruiterDocumentRepository.deleteById(id);
        return ResponseEntity.ok(java.util.Map.of("message", "Document deleted successfully"));
    }
}