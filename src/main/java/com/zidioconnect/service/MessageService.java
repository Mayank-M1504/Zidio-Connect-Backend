package com.zidioconnect.service;

import com.zidioconnect.model.Message;
import com.zidioconnect.model.Application;
import com.zidioconnect.repository.MessageRepository;
import com.zidioconnect.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ApplicationRepository applicationRepository;

    public List<Message> getMessagesForApplication(Long applicationId) {
        Application app = applicationRepository.findById(applicationId).orElse(null);
        if (app == null)
            return List.of();
        return messageRepository.findByApplicationOrderByTimestampAsc(app);
    }

    public Message sendMessage(Long applicationId, Long senderId, String senderRole, String content) {
        Application app = applicationRepository.findById(applicationId).orElseThrow();
        Message msg = new Message();
        msg.setApplication(app);
        msg.setSenderId(senderId);
        msg.setSenderRole(senderRole);
        msg.setContent(content);
        return messageRepository.save(msg);
    }
}