package com.example.service;

import com.example.model.dto.RegistrationRequest;
import com.example.model.entity.User;
import com.example.repository.UserRepository;
import com.example.exception.UserAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import com.example.model.dto.LoginRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(RegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User with email {} already exists", request.getEmail());
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);
        log.info("Successfully registered user with email: {}", request.getEmail());
        
        return savedUser;
    }

    public ResponseEntity<?> login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.ok().build();
        } else {
            throw new RuntimeException("Invalid password");
        }
    }
} 