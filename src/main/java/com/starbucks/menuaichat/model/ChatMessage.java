package com.starbucks.menuaichat.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDateTime;

@Table("chat_messages")
public class ChatMessage {
    
    @Id
    private Long id;
    
    @Column("session_id")
    private Long sessionId;
    
    @Column("message_type")
    private MessageType messageType;
    
    @Column("content")
    private String content;
    
    @Column("timestamp")
    private LocalDateTime timestamp;

    public enum MessageType {
        USER, ASSISTANT
    }

    // Constructors
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }

    public ChatMessage(MessageType messageType, String content) {
        this();
        this.messageType = messageType;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSessionId() { return sessionId; }
    public void setSessionId(Long sessionId) { this.sessionId = sessionId; }

    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}