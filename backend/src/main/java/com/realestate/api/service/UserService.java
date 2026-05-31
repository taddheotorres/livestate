package com.realestate.api.service;

import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.User;
import com.realestate.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Transactional
    public User updateCurrentUser(String email, User updatedDetails) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        user.setName(updatedDetails.getName());
        user.setBio(updatedDetails.getBio());
        if (updatedDetails.getPhone() != null) {
            user.setPhone(updatedDetails.getPhone());
        }
        return userRepository.save(user);
    }
}
