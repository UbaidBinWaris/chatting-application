package com.chattingapp.service;

import com.chattingapp.model.Message;
import com.chattingapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderByCreatedAtAsc();
    }

    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }
}
