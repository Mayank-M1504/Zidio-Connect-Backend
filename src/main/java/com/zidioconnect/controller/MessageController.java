package com.zidioconnect.controller;

import com.zidioconnect.dto.MessageRequest;
import com.zidioconnect.dto.MessageResponse;
import com.zidioconnect.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<?> sendMessage(Authentication authentication, @RequestBody MessageRequest request) {
        // Determine sender role (could be improved by checking user type from DB)
        String senderRole = "STUDENT"; // Default
        // You may want to check if the authenticated user is a recruiter or student
        // For now, infer from receiverRole (if sending to recruiter, sender is student,
        // etc.)
        if (request.receiverRole != null && request.receiverRole.equalsIgnoreCase("STUDENT")) {
            senderRole = "RECRUITER";
        }
        MessageResponse resp = messageService.sendMessage(authentication, request, senderRole);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long applicationId) {
        List<MessageResponse> messages = messageService.getMessagesForApplication(applicationId);
        return ResponseEntity.ok(messages);
    }
}