package com.zidioconnect.repository;

import com.zidioconnect.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByApplicationId(Long applicationId);
    List<Message> findBySenderEmailAndReceiverEmail(String senderEmail, String receiverEmail);
    List<Message> findByReceiverEmail(String receiverEmail);
    List<Message> findBySenderEmail(String senderEmail);
}