package com.zidioconnect.service;

import com.zidioconnect.dto.MessageRequest;
import com.zidioconnect.dto.MessageResponse;
import com.zidioconnect.model.Message;
import com.zidioconnect.repository.MessageRepository;
import com.zidioconnect.repository.StudentRepository;
import com.zidioconnect.repository.RecruiterRepository;
import com.zidioconnect.repository.StudentProfileRepository;
import com.zidioconnect.repository.RecruiterProfileRepository;
import com.zidioconnect.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    @Autowired
    private RecruiterProfileRepository recruiterProfileRepository;

    public MessageResponse sendMessage(Authentication authentication, MessageRequest request, String senderRole) {
        String senderEmail = authentication.getName();
        System.out.println("DEBUG: senderEmail=" + senderEmail + ", senderRole=" + senderRole + ", principalClass="
                + authentication.getPrincipal().getClass().getName());
        // Basic validation (receiver details must be present)
        if (request.receiverEmail == null || request.receiverRole == null || request.content == null) {
            throw new IllegalArgumentException("Receiver details and content are required");
        }
        Message message = new Message();
        message.setSenderEmail(senderEmail);
        message.setSenderRole(senderRole);
        message.setReceiverEmail(request.receiverEmail);
        message.setReceiverRole(request.receiverRole);
        message.setContent(EncryptionUtil.encrypt(request.content));
        message.setApplicationId(request.applicationId);
        Message saved = messageRepository.save(message);
        return toResponse(saved);
    }

    public List<MessageResponse> getMessagesForApplication(Long applicationId) {
        return messageRepository.findByApplicationId(applicationId).stream()
                .map(msg -> toResponseWithName(msg))
                .collect(java.util.stream.Collectors.toList());
    }

    private MessageResponse toResponseWithName(Message message) {
        MessageResponse resp = new MessageResponse();
        resp.id = message.getId();
        resp.senderEmail = message.getSenderEmail();
        resp.senderRole = message.getSenderRole();
        resp.senderName = getSenderName(message.getSenderEmail(), message.getSenderRole());
        resp.receiverEmail = message.getReceiverEmail();
        resp.receiverRole = message.getReceiverRole();
        resp.content = EncryptionUtil.decrypt(message.getContent());
        resp.sentAt = message.getSentAt();
        resp.applicationId = message.getApplicationId();
        return resp;
    }

    private String getSenderName(String email, String role) {
        if (role != null && role.equalsIgnoreCase("RECRUITER")) {
            // Use recruiter profile for name
            return recruiterProfileRepository.findByEmail(email)
                    .map(p -> (p.getFirstName() != null ? p.getFirstName() : "")
                            + (p.getLastName() != null ? " " + p.getLastName() : ""))
                    .orElse(email);
        } else {
            // Use student profile for name
            return studentProfileRepository.findByEmail(email)
                    .map(p -> (p.getFirstName() != null ? p.getFirstName() : "")
                            + (p.getLastName() != null ? " " + p.getLastName() : ""))
                    .orElse(email);
        }
    }

    private MessageResponse toResponse(Message message) {
        MessageResponse resp = new MessageResponse();
        resp.id = message.getId();
        resp.senderEmail = message.getSenderEmail();
        resp.senderRole = message.getSenderRole();
        resp.senderName = getSenderName(message.getSenderEmail(), message.getSenderRole());
        resp.receiverEmail = message.getReceiverEmail();
        resp.receiverRole = message.getReceiverRole();
        resp.content = message.getContent();
        resp.sentAt = message.getSentAt();
        resp.applicationId = message.getApplicationId();
        return resp;
    }
}