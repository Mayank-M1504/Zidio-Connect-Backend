package com.zidioconnect.controller;

import com.zidioconnect.model.RecruiterJob;
import com.zidioconnect.service.RecruiterJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/jobs")
public class AdminJobController {
    @Autowired
    private RecruiterJobService jobService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RecruiterJob>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }
}