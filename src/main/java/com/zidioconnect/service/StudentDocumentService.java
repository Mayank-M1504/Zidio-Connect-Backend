package com.zidioconnect.service;

import com.zidioconnect.model.StudentDocument;
import com.zidioconnect.model.StudentCertificate;
import com.zidioconnect.model.StudentProfile;
import com.zidioconnect.model.Student;
import com.zidioconnect.repository.StudentDocumentRepository;
import com.zidioconnect.repository.StudentCertificateRepository;
import com.zidioconnect.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class StudentDocumentService {
    @Autowired
    private StudentDocumentRepository documentRepository;

    @Autowired
    private StudentCertificateRepository certificateRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FileUploadService fileUploadService;

    public StudentDocument uploadDocument(MultipartFile file, String type, StudentProfile profile) throws IOException {
        System.out.println("=== UPLOADING DOCUMENT ===");
        System.out.println("File: " + file.getOriginalFilename());
        System.out.println("Type: " + type);
        System.out.println("Profile ID: " + profile.getId());
        System.out.println("Profile Email: " + profile.getEmail());

        // Validate file
        if (!fileUploadService.isValidDocument(file, type)) {
            throw new IllegalArgumentException("Invalid file type for " + type);
        }

        // Upload to Cloudinary
        String url = fileUploadService.uploadDocument(file, type);
        System.out.println("Cloudinary URL: " + url);

        // Create document record
        StudentDocument doc = new StudentDocument();
        doc.setProfile(profile);
        doc.setType(type);
        doc.setName(type.replace("_", " ").toUpperCase());
        doc.setUrl(url);
        doc.setFileName(file.getOriginalFilename());
        doc.setFileSize(file.getSize());
        doc.setContentType(file.getContentType());

        StudentDocument savedDoc = documentRepository.save(doc);
        System.out.println("Document saved with ID: " + savedDoc.getId());
        System.out.println("Total documents in DB: " + documentRepository.count());

        // If uploading a profile picture, update the Student entity
        if ("profile_picture".equals(type)) {
            Student student = profile.getStudent();
            if (student != null) {
                student.setProfilePicture(url);
                studentRepository.save(student);
            }
        }

        return savedDoc;
    }

    public StudentCertificate uploadCertificate(MultipartFile file, String certificateName, StudentProfile profile)
            throws IOException {
        System.out.println("=== UPLOADING CERTIFICATE ===");
        System.out.println("File: " + file.getOriginalFilename());
        System.out.println("Certificate Name: " + certificateName);
        System.out.println("Profile ID: " + profile.getId());
        System.out.println("Profile Email: " + profile.getEmail());

        // Validate file
        if (!fileUploadService.isValidDocument(file, "certificate")) {
            throw new IllegalArgumentException("Invalid file type for certificate");
        }

        // Upload to Cloudinary
        String url = fileUploadService.uploadDocument(file, "certificate");
        System.out.println("Cloudinary URL: " + url);

        // Create certificate record
        StudentCertificate cert = new StudentCertificate();
        cert.setProfile(profile);
        cert.setCertificateName(certificateName);
        cert.setUrl(url);
        cert.setFileName(file.getOriginalFilename());
        cert.setFileSize(file.getSize());
        cert.setContentType(file.getContentType());

        StudentCertificate savedCert = certificateRepository.save(cert);
        System.out.println("Certificate saved with ID: " + savedCert.getId());
        System.out.println("Total certificates in DB: " + certificateRepository.count());

        return savedCert;
    }

    public List<StudentDocument> getDocumentsByProfileId(Long profileId) {
        return documentRepository.findByProfileId(profileId);
    }

    public List<StudentDocument> getDocumentsByProfileIdAndType(Long profileId, String type) {
        return documentRepository.findByProfileIdAndType(profileId, type);
    }

    public List<StudentCertificate> getCertificatesByProfileId(Long profileId) {
        return certificateRepository.findByProfileId(profileId);
    }

    public StudentDocument getLatestDocumentByType(Long profileId, String type) {
        return documentRepository.findByProfileIdAndTypeAndStatus(profileId, type, "PENDING")
                .orElse(null);
    }

    public boolean deleteDocument(Long documentId, Long profileId) {
        StudentDocument doc = documentRepository.findById(documentId).orElse(null);
        if (doc != null && doc.getProfile().getId().equals(profileId)) {
            // Delete from Cloudinary
            String publicId = fileUploadService.extractPublicIdFromUrl(doc.getUrl());
            if (publicId != null) {
                fileUploadService.deleteFile(publicId, "raw");
            }
            documentRepository.delete(doc);
            return true;
        }
        return false;
    }

    public boolean deleteCertificate(Long certificateId, Long profileId) {
        StudentCertificate cert = certificateRepository.findById(certificateId).orElse(null);
        if (cert != null && cert.getProfile().getId().equals(profileId)) {
            // Delete from Cloudinary
            String publicId = fileUploadService.extractPublicIdFromUrl(cert.getUrl());
            if (publicId != null) {
                fileUploadService.deleteFile(publicId, "raw");
            }
            certificateRepository.delete(cert);
            return true;
        }
        return false;
    }

    public List<StudentDocument> getAllDocuments() {
        System.out.println("=== GETTING ALL DOCUMENTS ===");
        List<StudentDocument> docs = documentRepository.findAll();
        System.out.println("Found " + docs.size() + " documents");

        // Force load the profile relationships to avoid lazy loading issues
        for (StudentDocument doc : docs) {
            try {
                // This will trigger the lazy loading
                StudentProfile profile = doc.getProfile();
                if (profile != null) {
                    System.out.println("Document ID: " + doc.getId() + ", Type: " + doc.getType() + ", File: "
                            + doc.getFileName() + ", Profile: " + profile.getEmail());
                } else {
                    System.out.println("Document ID: " + doc.getId() + ", Type: " + doc.getType() + ", File: "
                            + doc.getFileName() + ", Profile: null");
                }
            } catch (Exception e) {
                System.out.println("Error loading profile for document " + doc.getId() + ": " + e.getMessage());
            }
        }
        return docs;
    }

    public List<StudentCertificate> getAllCertificates() {
        System.out.println("=== GETTING ALL CERTIFICATES ===");
        List<StudentCertificate> certs = certificateRepository.findAll();
        System.out.println("Found " + certs.size() + " certificates");

        // Force load the profile relationships to avoid lazy loading issues
        for (StudentCertificate cert : certs) {
            try {
                // This will trigger the lazy loading
                StudentProfile profile = cert.getProfile();
                if (profile != null) {
                    System.out.println("Certificate ID: " + cert.getId() + ", Name: " + cert.getCertificateName()
                            + ", File: " + cert.getFileName() + ", Profile: " + profile.getEmail());
                } else {
                    System.out.println("Certificate ID: " + cert.getId() + ", Name: " + cert.getCertificateName()
                            + ", File: " + cert.getFileName() + ", Profile: null");
                }
            } catch (Exception e) {
                System.out.println("Error loading profile for certificate " + cert.getId() + ": " + e.getMessage());
            }
        }
        return certs;
    }

    public StudentDocument saveDocument(StudentDocument doc) {
        return documentRepository.save(doc);
    }

    public StudentCertificate saveCertificate(StudentCertificate cert) {
        return certificateRepository.save(cert);
    }
}