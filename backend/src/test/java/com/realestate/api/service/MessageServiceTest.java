package com.realestate.api.service;

import com.realestate.api.config.AbstractIntegrationTest;
import com.realestate.api.model.Message;
import com.realestate.api.model.Role;
import com.realestate.api.model.User;
import com.realestate.api.repository.MessageRepository;
import com.realestate.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MessageServiceTest extends AbstractIntegrationTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        userRepository.deleteAll();

        user1 = User.builder()
                .name("User One").email("one@test.com")
                .password("pwd").role(Role.USER).build();
        user1 = userRepository.save(user1);

        user2 = User.builder()
                .name("User Two").email("two@test.com")
                .password("pwd").role(Role.USER).build();
        user2 = userRepository.save(user2);
    }

    @Test
    void shouldSendMessage() {
        Message msg = messageService.sendMessage(user1, user2, "Hello!", null);

        assertNotNull(msg);
        assertNotNull(msg.getId());
        assertEquals("Hello!", msg.getContent());
        assertEquals(user1.getId(), msg.getSender().getId());
        assertEquals(user2.getId(), msg.getReceiver().getId());
        assertFalse(msg.isReadMessage());
    }

    @Test
    void shouldGetConversation() {
        messageService.sendMessage(user1, user2, "Hi", null);
        messageService.sendMessage(user2, user1, "Hey!", null);

        var conversation = messageService.getConversation(user1.getId(), user2.getId());
        assertEquals(2, conversation.size());
    }

    @Test
    void shouldMarkAsRead() {
        messageService.sendMessage(user2, user1, "Unread", null);
        messageService.markAsRead(user1.getId(), user2.getId());

        var conversation = messageService.getConversation(user1.getId(), user2.getId());
        assertTrue(conversation.stream().allMatch(Message::isReadMessage));
    }
}
