package com.zidioconnect.dto;

import java.time.LocalDateTime;

public class MessageResponse {
    public Long id;
    public String senderEmail;
    public String senderRole;
    public String senderName;
    public String receiverEmail;
    public String receiverRole;
    public String content;
    public LocalDateTime sentAt;
    public Long applicationId;
}