package com.zidioconnect.controller;

import com.zidioconnect.model.Message;
import com.zidioconnect.model.Application;
import com.zidioconnect.model.Student;
import com.zidioconnect.model.Recruiter;
import com.zidioconnect.repository.ApplicationRepository;
import com.zidioconnect.repository.StudentRepository;
import com.zidioconnect.repository.RecruiterRepository;
import com.zidioconnect.service.MessageService;
import com.zidioconnect.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private RecruiterRepository recruiterRepository;

    @GetMapping("/{applicationId}")
    public ResponseEntity<?> getMessages(Authentication authentication, @PathVariable Long applicationId) {
        String email = authentication.getName();
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null)
            return ResponseEntity.notFound().build();
        boolean isStudent = app.getStudentProfile().getStudent().getEmail().equals(email);
        boolean isRecruiter = app.getJob().getRecruiter().getEmail().equals(email);
        if (!isStudent && !isRecruiter)
            return ResponseEntity.status(403).body("Unauthorized");
        List<Message> messages = messageService.getMessagesForApplication(applicationId);
        // Decrypt content before returning
        List<Message> decrypted = messages.stream().map(msg -> {
            try {
                msg.setContent(EncryptionUtil.decrypt(msg.getContent()));
            } catch (Exception e) {
                msg.setContent("[decryption error]");
            }
            return msg;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(decrypted);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(Authentication authentication, @RequestBody Map<String, Object> payload) {
        String email = authentication.getName();
        System.out.println("[MessageController] Incoming sendMessage payload: " + payload);
        Long applicationId = Long.valueOf(payload.get("applicationId").toString());
        String content = payload.get("content") != null ? payload.get("content").toString() : null;
        System.out.println("[MessageController] applicationId=" + applicationId + ", content=" + content);
        if (content == null || content.trim().isEmpty()) {
            System.out.println("[MessageController] Message content is empty or null!");
            return ResponseEntity.badRequest().body("Message content cannot be empty");
        }
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null) {
            System.out.println("[MessageController] Application not found for id: " + applicationId);
            return ResponseEntity.notFound().build();
        }
        boolean isStudent = app.getStudentProfile().getStudent().getEmail().equals(email);
        boolean isRecruiter = app.getJob().getRecruiter().getEmail().equals(email);
        if (!isStudent && !isRecruiter) {
            System.out.println("[MessageController] Unauthorized message attempt by: " + email);
            return ResponseEntity.status(403).body("Unauthorized");
        }
        Long senderId;
        String senderRole;
        if (isStudent) {
            senderId = app.getStudentProfile().getStudent().getId();
            senderRole = "STUDENT";
        } else {
            senderId = app.getJob().getRecruiter().getId();
            senderRole = "RECRUITER";
        }
        try {
            String encryptedContent = EncryptionUtil.encrypt(content);
            Message msg = messageService.sendMessage(applicationId, senderId, senderRole, encryptedContent);
            msg.setContent(content); // Return decrypted content to frontend
            System.out.println("[MessageController] Message sent successfully for applicationId=" + applicationId);
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[MessageController] Exception during message send:");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Encryption error: " + e.getMessage());
        }
    }
}