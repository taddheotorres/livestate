package com.realestate.api.service;

import com.realestate.api.dto.AuthenticationRequest;
import com.realestate.api.dto.AuthenticationResponse;
import com.realestate.api.dto.RegisterRequest;
import com.realestate.api.model.Role;
import com.realestate.api.model.User;
import com.realestate.api.repository.UserRepository;
import com.realestate.api.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .rating(5.0)
                .reviewsCount(0)
                .recommended(true)
                .build();

        userRepository.save(user);

        log.info("User registered: {}", request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User logged in: {}", request.getEmail());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public AuthenticationResponse refreshToken(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var newToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        log.info("Token refreshed for user: {}", userEmail);
        return AuthenticationResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
