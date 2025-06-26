package com.zidioconnect.service;

import com.zidioconnect.dto.AuthResponse;
import com.zidioconnect.dto.ForgotPasswordRequest;
import com.zidioconnect.dto.LoginRequest;
import com.zidioconnect.dto.RegisterRequest;
import com.zidioconnect.dto.ResetPasswordRequest;
import com.zidioconnect.model.PasswordResetToken;
import com.zidioconnect.model.User;
import com.zidioconnect.model.UserRole;
import com.zidioconnect.repository.PasswordResetTokenRepository;
import com.zidioconnect.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        try {
            // Check if passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already registered");
            }

            // Create new user
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            // Split name into first and last name
            String[] nameParts = request.getName().split(" ", 2);
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");

            // Set college
            user.setCollege(request.getCollege());

            // Set role from request
            user.setRole(request.getRole());

            // Save user to database
            userRepository.save(user);

            // Generate JWT token
            String token = tokenProvider.generateToken(user.getEmail());

            return new AuthResponse(token, "User registered successfully");
        } catch (Exception e) {
            logger.error("Error during user registration: ", e);
            throw new RuntimeException("Error during user registration: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request) {
        try {
            // Find user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid email or password");
            }

            // Increment login count and update last login time
            user.setLoginCount(user.getLoginCount() + 1);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // Log the login information
            logger.info("User login successful - Email: {}, Login Count: {}, Last Login: {}",
                    user.getEmail(), user.getLoginCount(), user.getLastLoginAt());

            // Generate JWT token
            String token = tokenProvider.generateToken(user.getEmail());

            return new AuthResponse(token, "Login successful");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during user login: ", e);
            throw new RuntimeException("Error during user login: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("No user found with this email"));

            // Delete any existing tokens for this user
            tokenRepository.deleteByUser_Id(user.getId());

            // Create new token
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setToken(UUID.randomUUID().toString());
            resetToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours
            tokenRepository.save(resetToken);

            // In a real application, you would send an email here
            // For now, we'll just log the reset link
            String resetLink = "http://localhost:3000/reset-password?token=" + resetToken.getToken();
            logger.info("Password reset link for user {}: {}", user.getEmail(), resetLink);

            // TODO: Send email with reset link
            // emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
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

            // Get the user
            User user = resetToken.getUser();

            // Update password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);

            // Mark token as used
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);

            // Generate new JWT token
            String token = tokenProvider.generateToken(user.getEmail());

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
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            logger.info("User Login Statistics - Email: {}, Total Logins: {}, Last Login: {}",
                    user.getEmail(), user.getLoginCount(), user.getLastLoginAt());
        } catch (Exception e) {
            logger.error("Error logging user stats: {}", e.getMessage());
        }
    }
}