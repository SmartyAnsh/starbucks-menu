package com.starbucks.menuaichat.controller;

import com.starbucks.menuaichat.service.StarbucksAiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Autowired
    private StarbucksAiChatService chatService;
    
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startNewSession() {
        logger.info("🚀 Starting new chat session");
        String sessionId = chatService.startNewSession();
        logger.info("✅ Created new session: {}", sessionId);
        return ResponseEntity.ok(Map.of(
            "sessionId", sessionId,
            "message", "Welcome to Starbucks AI Menu Assistant! How can I help you find the perfect drink today?"
        ));
    }
    
    @PostMapping("/message")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody ChatRequest request) {
        logger.info("💬 Received message for session: {} - Message: {}", 
            request.getSessionId(), request.getMessage());
        
        try {
            String response = chatService.chat(request.getSessionId(), request.getMessage());
            logger.info("✅ Successfully processed message for session: {}", request.getSessionId());
            return ResponseEntity.ok(Map.of(
                "sessionId", request.getSessionId(),
                "response", response
            ));
        } catch (Exception e) {
            logger.error("❌ Error processing message for session: {} - Error: {}", 
                request.getSessionId(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to process message: " + e.getMessage()
            ));
        }
    }
    
    public static class ChatRequest {
        private String sessionId;
        private String message;
        
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}