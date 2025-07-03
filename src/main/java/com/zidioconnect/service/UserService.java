package com.zidioconnect.service;

import com.zidioconnect.dto.AuthResponse;
import com.zidioconnect.dto.ForgotPasswordRequest;
import com.zidioconnect.dto.LoginRequest;
import com.zidioconnect.dto.RegisterRequest;
import com.zidioconnect.dto.ResetPasswordRequest;
import com.zidioconnect.model.Student;
import com.zidioconnect.model.PasswordResetToken;
import com.zidioconnect.repository.StudentRepository;
import com.zidioconnect.repository.PasswordResetTokenRepository;
import com.zidioconnect.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private EmailService emailService;

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        try {
            if (studentRepository.existsByEmail(request.getEmail())) {
                return new AuthResponse(null, "Email already registered");
            }
            // Create new student
            Student student = new Student();
            student.setEmail(request.getEmail());
            student.setPassword(passwordEncoder.encode(request.getPassword()));
            String[] nameParts = request.getName().split(" ", 2);
            student.setFirstName(nameParts[0]);
            student.setLastName(nameParts.length > 1 ? nameParts[1] : "");
            student.setCollege(request.getCollege());
            student.setIsActive(true);
            student.setCreatedAt(LocalDateTime.now());
            student.setUpdatedAt(LocalDateTime.now());
            // Save student to database
            studentRepository.save(student);
            String token = tokenProvider.generateToken(student.getEmail());
            return new AuthResponse(token, "Student registered successfully");
        } catch (Exception e) {
            logger.error("Error during student registration: ", e);
            throw new RuntimeException("Error during student registration: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request) {
        try {
            // Find student by email
            Student student = studentRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
            if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
                throw new IllegalArgumentException("Invalid email or password");
            }
            student.setUpdatedAt(LocalDateTime.now());
            studentRepository.save(student);
            String token = tokenProvider.generateToken(student.getEmail());
            return new AuthResponse(token, "Login successful");
        } catch (Exception e) {
            logger.error("Error during student login: ", e);
            throw new RuntimeException("Error during student login: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        try {
            Student student = studentRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("No student found with this email"));

            // Delete any existing tokens for this student
            tokenRepository.deleteByStudent_Id(student.getId());

            // Create new token
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setStudent(student);
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours
            tokenRepository.save(resetToken);

            // Send email with reset link
            String resetLink = "http://localhost:3000/reset-password?token=" + resetToken.getToken();
            String subject = "Password Reset Request";
            String text = "Dear " + student.getFirstName() + ",\n\n" +
                    "We received a request to reset your password. Please use the following link to reset your password: \n"
                    +
                    resetLink + "\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\nZidioConnect Team";
            emailService.sendSimpleMessage(student.getEmail(), subject, text);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during forgot password process: ", e);
            throw new RuntimeException("Error processing forgot password request: " + e.getMessage(), e);
        }
    }

    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        try {
            // Validate passwords match
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }

            // Find the token
            PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

            // Check if token is expired
            if (resetToken.isExpired()) {
                tokenRepository.delete(resetToken);
                throw new IllegalArgumentException("Reset token has expired");
            }

            // Check if token has been used
            if (resetToken.isUsed()) {
                throw new IllegalArgumentException("Reset token has already been used");
            }

            // Get the student
            Student student = resetToken.getStudent();

            // Update password
            student.setPassword(passwordEncoder.encode(request.getNewPassword()));
            studentRepository.save(student);

            // Mark token as used
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);

            // Generate new JWT token
            String token = tokenProvider.generateToken(student.getEmail());

            return new AuthResponse(token, "Password has been reset successfully");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during password reset: ", e);
            throw new RuntimeException("Error resetting password: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public void logUserLoginStats(String email) {
        try {
            Student student = studentRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            logger.info("Student Login Statistics - Email: {}, Last Login: {}", student.getEmail(),
                    student.getUpdatedAt());
        } catch (Exception e) {
            logger.error("Error logging student stats: {}", e.getMessage());
        }
    }
}