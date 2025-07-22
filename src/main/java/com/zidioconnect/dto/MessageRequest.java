package com.zidioconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageRequest {
    @NotBlank
    public String receiverEmail;

    @NotBlank
    public String receiverRole; // STUDENT or RECRUITER

    @NotBlank
    public String content;

    public Long applicationId; // Optional, for context
} 