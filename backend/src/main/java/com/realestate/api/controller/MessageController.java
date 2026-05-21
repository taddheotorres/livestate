package com.realestate.api.controller;

import com.realestate.api.model.Message;
import com.realestate.api.model.Property;
import com.realestate.api.model.User;
import com.realestate.api.repository.MessageRepository;
import com.realestate.api.repository.PropertyRepository;
import com.realestate.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User sender = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Sender no encontrado"));

        Long receiverId = Long.valueOf(body.get("receiverId").toString());
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver no encontrado"));

        Message msg = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(body.get("content").toString())
                .build();

        // Adjuntar propiedad si se especifica
        if (body.containsKey("propertyId") && body.get("propertyId") != null) {
            Long propertyId = Long.valueOf(body.get("propertyId").toString());
            propertyRepository.findById(propertyId).ifPresent(msg::setProperty);
        }

        Message savedMsg = messageRepository.save(msg);

        // Broadcast a través de WebSocket al receptor
        messagingTemplate.convertAndSendToUser(
                receiverId.toString(),
                "/queue/messages",
                savedMsg
        );

        return ResponseEntity.ok(savedMsg);
    }

    // GET conversación con otro usuario
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<Message>> getConversation(@PathVariable Long otherUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User me = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ResponseEntity.ok(messageRepository.findConversation(me.getId(), otherUserId));
    }

    // Marcar mensajes como leídos
    @PutMapping("/read/{senderId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long senderId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User me = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Message> unread = messageRepository.findConversation(me.getId(), senderId)
                .stream()
                .filter(m -> m.getReceiver().getId().equals(me.getId()) && !m.isRead())
                .toList();

        unread.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unread);
        return ResponseEntity.noContent().build();
    }
}
