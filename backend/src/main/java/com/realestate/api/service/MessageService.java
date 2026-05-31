package com.realestate.api.service;

import com.realestate.api.exception.ResourceNotFoundException;
import com.realestate.api.model.Message;
import com.realestate.api.model.User;
import com.realestate.api.repository.MessageRepository;
import com.realestate.api.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final PropertyRepository propertyRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public Message sendMessage(User sender, User receiver, String content, Long propertyId) {
        Message msg = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();

        if (propertyId != null) {
            propertyRepository.findById(propertyId).ifPresent(msg::setProperty);
        }

        Message savedMsg = messageRepository.save(msg);

        messagingTemplate.convertAndSendToUser(
                receiver.getId().toString(),
                "/queue/messages",
                savedMsg
        );

        return savedMsg;
    }

    public List<Message> getConversation(Long userId1, Long userId2) {
        return messageRepository.findConversation(userId1, userId2);
    }

    @Transactional
    public void markAsRead(Long myId, Long senderId) {
        List<Message> unread = messageRepository.findConversation(myId, senderId)
                .stream()
                .filter(m -> m.getReceiver().getId().equals(myId) && !m.isReadMessage())
                .toList();

        unread.forEach(m -> m.setReadMessage(true));
        messageRepository.saveAll(unread);
    }
}
