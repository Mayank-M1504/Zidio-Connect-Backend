package com.zidioconnect.controller;

import com.zidioconnect.model.Student;
import com.zidioconnect.model.StudentProfile;
import com.zidioconnect.model.StudentDocument;
import com.zidioconnect.model.StudentCertificate;
import com.zidioconnect.repository.StudentProfileRepository;
import com.zidioconnect.service.StudentDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.zidioconnect.repository.RecruiterDocumentRepository;
import com.zidioconnect.model.RecruiterDocument;
import com.zidioconnect.dto.DocumentDTO;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    @Autowired
    private StudentProfileRepository profileRepository;
    @Autowired
    private StudentDocumentService documentService;
    @Autowired
    private RecruiterDocumentRepository recruiterDocumentRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type) {
        String email = authentication.getName();
        StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
        try {
            StudentDocument doc = documentService.uploadDocument(file, type, profile);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Document uploaded successfully");
            response.put("document", doc);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/certificates/upload")
    public ResponseEntity<?> uploadCertificate(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam("certificateName") String certificateName) {
        String email = authentication.getName();
        StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
        try {
            StudentCertificate cert = documentService.uploadCertificate(file, certificateName, profile);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Certificate uploaded successfully");
            response.put("certificate", cert);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getDocuments(Authentication authentication) {
        String email = authentication.getName();
        StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
        try {
            List<StudentDocument> documents = documentService.getDocumentsByProfileId(profile.getId());
            List<StudentCertificate> certificates = documentService.getCertificatesByProfileId(profile.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("documents", documents);
            response.put("certificates", certificates);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getDocumentsByType(
            Authentication authentication,
            @PathVariable String type) {
        String email = authentication.getName();
        StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
        try {
            List<StudentDocument> documents = documentService.getDocumentsByProfileIdAndType(profile.getId(), type);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<?> deleteDocument(
            Authentication authentication,
            @PathVariable Long documentId) {
        String email = authentication.getName();
        StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
        try {
            boolean deleted = documentService.deleteDocument(documentId, profile.getId());
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Document not found or access denied"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/certificates/{certificateId}")
    public ResponseEntity<?> deleteCertificate(
            Authentication authentication,
            @PathVariable Long certificateId) {
        String email = authentication.getName();
        StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
        try {
            boolean deleted = documentService.deleteCertificate(certificateId, profile.getId());
            if (deleted) {
                return ResponseEntity.ok(Map.of("message", "Certificate deleted successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Certificate not found or access denied"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Test endpoint to check database state
    @GetMapping("/test/db-state")
    public ResponseEntity<?> getDatabaseState() {
        try {
            System.out.println("=== TESTING DATABASE STATE ===");

            // Get all documents
            List<StudentDocument> allDocs = documentService.getAllDocuments();
            System.out.println("Total documents in DB: " + allDocs.size());

            // Get all certificates
            List<StudentCertificate> allCerts = documentService.getAllCertificates();
            System.out.println("Total certificates in DB: " + allCerts.size());

            // Create a simple response without lazy loading issues
            List<Map<String, Object>> docList = new ArrayList<>();
            for (StudentDocument doc : allDocs) {
                Map<String, Object> docMap = new HashMap<>();
                docMap.put("id", doc.getId());
                docMap.put("fileName", doc.getFileName());
                docMap.put("type", doc.getType());
                docMap.put("status", doc.getStatus());
                docMap.put("uploadedAt", doc.getUploadedAt());
                docMap.put("fileSize", doc.getFileSize());
                docMap.put("contentType", doc.getContentType());
                docMap.put("url", doc.getUrl());

                // Safely get profile info
                try {
                    StudentProfile profile = doc.getProfile();
                    if (profile != null) {
                        docMap.put("profileEmail", profile.getEmail());
                        docMap.put("profileId", profile.getId());
                    } else {
                        docMap.put("profileEmail", "null");
                        docMap.put("profileId", "null");
                    }
                } catch (Exception e) {
                    docMap.put("profileEmail", "error: " + e.getMessage());
                    docMap.put("profileId", "error");
                }

                docList.add(docMap);
            }

            List<Map<String, Object>> certList = new ArrayList<>();
            for (StudentCertificate cert : allCerts) {
                Map<String, Object> certMap = new HashMap<>();
                certMap.put("id", cert.getId());
                certMap.put("fileName", cert.getFileName());
                certMap.put("certificateName", cert.getCertificateName());
                certMap.put("status", cert.getStatus());
                certMap.put("uploadedAt", cert.getUploadedAt());
                certMap.put("fileSize", cert.getFileSize());
                certMap.put("contentType", cert.getContentType());
                certMap.put("url", cert.getUrl());

                // Safely get profile info
                try {
                    StudentProfile profile = cert.getProfile();
                    if (profile != null) {
                        certMap.put("profileEmail", profile.getEmail());
                        certMap.put("profileId", profile.getId());
                    } else {
                        certMap.put("profileEmail", "null");
                        certMap.put("profileId", "null");
                    }
                } catch (Exception e) {
                    certMap.put("profileEmail", "error: " + e.getMessage());
                    certMap.put("profileId", "error");
                }

                certList.add(certMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("documentCount", docList.size());
            response.put("certificateCount", certList.size());
            response.put("documents", docList);
            response.put("certificates", certList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in test endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ADMIN: Get all documents
    @GetMapping("/admin/all-documents")
    public ResponseEntity<?> getAllDocumentsAdmin() {
        System.out.println("=== ADMIN: Getting all documents ===");
        try {
            List<StudentDocument> documents = documentService.getAllDocuments();
            System.out.println("Found " + documents.size() + " documents");
            for (StudentDocument doc : documents) {
                System.out.println("Document ID: " + doc.getId() + ", Type: " + doc.getType() + ", File: "
                        + doc.getFileName() + ", Profile: " + doc.getProfile().getEmail());
            }
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            System.out.println("Error getting documents: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ADMIN: Get all certificates
    @GetMapping("/admin/all-certificates")
    public ResponseEntity<?> getAllCertificatesAdmin() {
        System.out.println("=== ADMIN: Getting all certificates ===");
        try {
            List<StudentCertificate> certificates = documentService.getAllCertificates();
            System.out.println("Found " + certificates.size() + " certificates");
            for (StudentCertificate cert : certificates) {
                System.out.println("Certificate ID: " + cert.getId() + ", Name: " + cert.getCertificateName()
                        + ", File: " + cert.getFileName() + ", Profile: " + cert.getProfile().getEmail());
            }
            return ResponseEntity.ok(certificates);
        } catch (Exception e) {
            System.out.println("Error getting certificates: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ADMIN: Update document status
    @PatchMapping("/admin/document-status/{documentId}")
    public ResponseEntity<?> updateDocumentStatus(@PathVariable Long documentId,
            @RequestParam("status") String status) {
        // Try student document first
        var docOpt = documentService.getAllDocuments().stream().filter(d -> d.getId().equals(documentId)).findFirst();
        if (docOpt.isPresent()) {
            var doc = docOpt.get();
            doc.setStatus(status);
            documentService.saveDocument(doc);
            DocumentDTO dto = new DocumentDTO();
            dto.id = doc.getId();
            dto.name = doc.getFileName();
            dto.url = doc.getUrl();
            dto.status = doc.getStatus();
            dto.type = doc.getType();
            return ResponseEntity.ok(Map.of("message", "Status updated", "document", dto));
        }
        // Try recruiter document
        var recruiterDocOpt = recruiterDocumentRepository.findById(documentId);
        if (recruiterDocOpt.isPresent()) {
            RecruiterDocument recruiterDoc = recruiterDocOpt.get();
            recruiterDoc.setStatus(status);
            recruiterDocumentRepository.save(recruiterDoc);
            DocumentDTO dto = new DocumentDTO();
            dto.id = recruiterDoc.getId();
            dto.name = recruiterDoc.getFileName();
            dto.url = recruiterDoc.getUrl();
            dto.status = recruiterDoc.getStatus();
            dto.type = recruiterDoc.getType();
            return ResponseEntity.ok(Map.of("message", "Status updated", "document", dto));
        }
        // Not found in either
        return ResponseEntity.notFound().build();
    }

    // ADMIN: Update certificate status
    @PatchMapping("/admin/certificate-status/{certificateId}")
    public ResponseEntity<?> updateCertificateStatus(@PathVariable Long certificateId,
            @RequestParam("status") String status) {
        var certOpt = documentService.getAllCertificates().stream().filter(c -> c.getId().equals(certificateId))
                .findFirst();
        if (certOpt.isEmpty())
            return ResponseEntity.notFound().build();
        var cert = certOpt.get();
        cert.setStatus(status);
        documentService.saveCertificate(cert);
        return ResponseEntity.ok(Map.of("message", "Status updated", "certificate", cert));
    }

    // Student endpoint to get documents without lazy loading issues
    @GetMapping("/student/list")
    public ResponseEntity<?> getStudentDocuments(Authentication authentication) {
        String email = authentication.getName();
        StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
        if (profile == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Profile not found"));
        }
        try {
            System.out.println("=== GETTING STUDENT DOCUMENTS ===");
            System.out.println("Student email: " + email);
            System.out.println("Profile ID: " + profile.getId());

            // Get documents for this student
            List<StudentDocument> studentDocs = documentService.getDocumentsByProfileId(profile.getId());
            List<StudentCertificate> studentCerts = documentService.getCertificatesByProfileId(profile.getId());

            System.out.println("Found " + studentDocs.size() + " documents for student");
            System.out.println("Found " + studentCerts.size() + " certificates for student");

            // Create a simple response without lazy loading issues
            List<Map<String, Object>> docList = new ArrayList<>();
            for (StudentDocument doc : studentDocs) {
                Map<String, Object> docMap = new HashMap<>();
                docMap.put("id", doc.getId());
                docMap.put("fileName", doc.getFileName());
                docMap.put("type", doc.getType());
                docMap.put("status", doc.getStatus());
                docMap.put("uploadedAt", doc.getUploadedAt());
                docMap.put("fileSize", doc.getFileSize());
                docMap.put("contentType", doc.getContentType());
                docMap.put("url", doc.getUrl());
                docMap.put("name", doc.getName());

                // Safely get profile info
                try {
                    StudentProfile docProfile = doc.getProfile();
                    if (docProfile != null) {
                        docMap.put("profileEmail", docProfile.getEmail());
                        docMap.put("profileId", docProfile.getId());
                    } else {
                        docMap.put("profileEmail", "null");
                        docMap.put("profileId", "null");
                    }
                } catch (Exception e) {
                    docMap.put("profileEmail", "error: " + e.getMessage());
                    docMap.put("profileId", "error");
                }

                docList.add(docMap);
            }

            List<Map<String, Object>> certList = new ArrayList<>();
            for (StudentCertificate cert : studentCerts) {
                Map<String, Object> certMap = new HashMap<>();
                certMap.put("id", cert.getId());
                certMap.put("fileName", cert.getFileName());
                certMap.put("certificateName", cert.getCertificateName());
                certMap.put("status", cert.getStatus());
                certMap.put("uploadedAt", cert.getUploadedAt());
                certMap.put("fileSize", cert.getFileSize());
                certMap.put("contentType", cert.getContentType());
                certMap.put("url", cert.getUrl());

                // Safely get profile info
                try {
                    StudentProfile certProfile = cert.getProfile();
                    if (certProfile != null) {
                        certMap.put("profileEmail", certProfile.getEmail());
                        certMap.put("profileId", certProfile.getId());
                    } else {
                        certMap.put("profileEmail", "null");
                        certMap.put("profileId", "null");
                    }
                } catch (Exception e) {
                    certMap.put("profileEmail", "error: " + e.getMessage());
                    certMap.put("profileId", "error");
                }

                certList.add(certMap);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("documentCount", docList.size());
            response.put("certificateCount", certList.size());
            response.put("documents", docList);
            response.put("certificates", certList);

            System.out.println("Returning " + docList.size() + " documents and " + certList.size() + " certificates");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in student documents endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Test endpoint to check current user and database state
    @GetMapping("/student/test")
    public ResponseEntity<?> testStudentAccess(Authentication authentication) {
        try {
            String email = authentication.getName();
            System.out.println("=== TESTING STUDENT ACCESS ===");
            System.out.println("Current user email: " + email);

            StudentProfile profile = profileRepository.findByEmail(email).orElse(null);
            System.out.println("Profile found: " + (profile != null));
            if (profile != null) {
                System.out.println("Profile ID: " + profile.getId());
            }

            // Get all documents and certificates
            List<StudentDocument> allDocs = documentService.getAllDocuments();
            List<StudentCertificate> allCerts = documentService.getAllCertificates();

            System.out.println("Total documents in DB: " + allDocs.size());
            System.out.println("Total certificates in DB: " + allCerts.size());

            // Count documents for this user
            long userDocCount = allDocs.stream()
                    .filter(doc -> {
                        try {
                            return doc.getProfile() != null && doc.getProfile().getEmail().equals(email);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .count();

            long userCertCount = allCerts.stream()
                    .filter(cert -> {
                        try {
                            return cert.getProfile() != null && cert.getProfile().getEmail().equals(email);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .count();

            System.out.println("Documents for user " + email + ": " + userDocCount);
            System.out.println("Certificates for user " + email + ": " + userCertCount);

            Map<String, Object> response = new HashMap<>();
            response.put("currentUser", email);
            response.put("profileFound", profile != null);
            response.put("profileId", profile != null ? profile.getId() : null);
            response.put("totalDocumentsInDB", allDocs.size());
            response.put("totalCertificatesInDB", allCerts.size());
            response.put("userDocuments", userDocCount);
            response.put("userCertificates", userCertCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Error in test endpoint: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}