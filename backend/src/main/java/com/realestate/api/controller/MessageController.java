package com.realestate.api.controller;

import com.realestate.api.dto.mapper.MessageMapper;
import com.realestate.api.dto.request.MessageRequest;
import com.realestate.api.dto.response.MessageResponse;
import com.realestate.api.model.User;
import com.realestate.api.security.SecurityUtils;
import com.realestate.api.service.MessageService;
import com.realestate.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User sender = userService.getCurrentUser(userEmail);
        User receiver = userService.getUserById(request.getReceiverId());

        return ResponseEntity.ok(
                messageMapper.toResponse(
                        messageService.sendMessage(sender, receiver, request.getContent(), request.getPropertyId())
                )
        );
    }

    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageResponse>> getConversation(@PathVariable Long otherUserId) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User me = userService.getCurrentUser(userEmail);
        return ResponseEntity.ok(
                messageMapper.toResponseList(messageService.getConversation(me.getId(), otherUserId))
        );
    }

    @PutMapping("/read/{senderId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long senderId) {
        String userEmail = SecurityUtils.getCurrentUserEmail();
        User me = userService.getCurrentUser(userEmail);
        messageService.markAsRead(me.getId(), senderId);
        return ResponseEntity.noContent().build();
    }
}
