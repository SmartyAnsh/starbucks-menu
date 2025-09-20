package com.starbucks.menuaichat.service;

import com.starbucks.menuaichat.model.ChatMessage;
import com.starbucks.menuaichat.model.ChatSession;
import com.starbucks.menuaichat.model.DrinkItem;
import com.starbucks.menuaichat.repository.ChatMessageRepository;
import com.starbucks.menuaichat.repository.ChatSessionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StarbucksAiChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(StarbucksAiChatService.class);
    
    @Autowired
    private org.springframework.ai.chat.model.ChatModel chatModel;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private ChatSessionRepository chatSessionRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    private static final String SYSTEM_PROMPT = """
        You are a helpful Starbucks menu assistant. You help customers find drinks based on their preferences, 
        dietary requirements, and nutritional needs. You have access to the complete Starbucks menu with detailed 
        nutritional information including calories, caffeine content, fat, protein, and other nutrients.
        
        When customers ask about drinks, you should:
        1. Understand their preferences (taste, size, milk type, etc.)
        2. Consider their dietary restrictions or nutritional goals
        3. Recommend suitable drinks with explanations
        4. Provide nutritional information when relevant
        5. Be friendly and conversational
        
        You can search for drinks by category, name, or nutritional criteria. Always provide helpful and accurate 
        information about Starbucks beverages.

        Be a human and keep the conversations short and sweet.

        Also, since you are responding in a chat - use bullets for suggesting different drinks
        """;
    
    public String chat(String sessionId, String userMessage) {
        logger.info("Starting chat for session: {} with message: {}", sessionId, userMessage);
        
        try {
            // Get or create chat session
            ChatSession session = getOrCreateSession(sessionId);
            logger.debug("Using chat session with ID: {}", session.getId());
            
            // Save user message
            saveMessage(session.getId(), ChatMessage.MessageType.USER, userMessage);
            logger.debug("Saved user message to database");
            
            // Get conversation history
            List<ChatMessage> history = chatMessageRepository.findBySessionIdOrderByTimestamp(session.getId());
            logger.debug("Retrieved {} messages from conversation history", history.size());
            
            // Analyze user message for menu queries
            String menuContext = analyzeAndGetMenuContext(userMessage);
            logger.debug("Generated menu context with {} characters", menuContext.length());
            
            // Build conversation prompt
            String conversationPrompt = buildConversationPrompt(history, menuContext, userMessage);
            logger.debug("Built conversation prompt with {} characters", conversationPrompt.length());
            
            // Get AI response using Spring AI
            logger.info("Sending request to Spring AI ChatModel for session: {}", sessionId);
            String aiResponse = chatModel.call(conversationPrompt);
            logger.info("Received AI response with {} characters for session: {}", aiResponse.length(), sessionId);
            
            // Save AI response
            saveMessage(session.getId(), ChatMessage.MessageType.ASSISTANT, aiResponse);
            logger.debug("Saved AI response to database");
            
            return aiResponse;
        } catch (Exception e) {
            logger.error("Error processing chat for session: {} - {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }
    
    private ChatSession getOrCreateSession(String sessionId) {
        Optional<ChatSession> existingSession = chatSessionRepository.findBySessionId(sessionId);
        if (existingSession.isPresent()) {
            logger.debug("Found existing session: {}", sessionId);
            return existingSession.get();
        }
        
        logger.info("Creating new chat session: {}", sessionId);
        ChatSession newSession = new ChatSession(sessionId);
        ChatSession savedSession = chatSessionRepository.save(newSession);
        logger.debug("Created new session with database ID: {}", savedSession.getId());
        return savedSession;
    }
    
    private void saveMessage(Long sessionId, ChatMessage.MessageType type, String content) {
        ChatMessage message = new ChatMessage(type, content);
        message.setSessionId(sessionId);
        chatMessageRepository.save(message);
    }
    
    private String analyzeAndGetMenuContext(String userMessage) {
        logger.debug("Analyzing user message for menu context using vector search: {}", userMessage);
        StringBuilder context = new StringBuilder();
        
        // Use vector search to find semantically similar drinks by description
        List<DrinkItem> similarDrinks = menuService.findSimilarDrinksByDescription(userMessage, 8);
        if (!similarDrinks.isEmpty()) {
            context.append("Relevant Drinks Based on Your Request:\n")
                   .append(menuService.formatDrinksForAI(similarDrinks))
                   .append("\n");
        }
        
        // If the message seems nutrition-focused, also search by nutritional similarity
        String lowerMessage = userMessage.toLowerCase();
        if (containsNutritionalKeywords(lowerMessage)) {
            List<DrinkItem> nutritionalMatches = menuService.findSimilarDrinksByNutrition(userMessage, 5);
            if (!nutritionalMatches.isEmpty()) {
                context.append("Nutritionally Similar Options:\n")
                       .append(menuService.formatDrinksForAI(nutritionalMatches))
                       .append("\n");
            }
        }
        
        logger.debug("Generated context with {} characters using vector search", context.length());
        return context.toString();
    }
    
    private boolean containsNutritionalKeywords(String message) {
        String[] nutritionalKeywords = {
            "calorie", "calories", "fat", "protein", "caffeine", "sugar", "carb", "carbohydrate",
            "diet", "healthy", "low", "high", "nutrition", "nutritional", "energy", "vitamin"
        };
        
        for (String keyword : nutritionalKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    private String buildConversationPrompt(List<ChatMessage> history, String menuContext, String userMessage) {
        StringBuilder prompt = new StringBuilder();
        
        // Add system prompt
        prompt.append(SYSTEM_PROMPT).append("\n\n");
        
        // Add menu context if available
        if (!menuContext.isEmpty()) {
            prompt.append("Current Menu Context:\n").append(menuContext).append("\n\n");
        }
        
        // Add conversation history (limit to last 6 messages to avoid token limits)
        int startIndex = Math.max(0, history.size() - 6);
        if (startIndex < history.size()) {
            prompt.append("Conversation History:\n");
            for (int i = startIndex; i < history.size(); i++) {
                ChatMessage msg = history.get(i);
                if (msg.getMessageType() == ChatMessage.MessageType.USER) {
                    prompt.append("User: ").append(msg.getContent()).append("\n");
                } else {
                    prompt.append("Assistant: ").append(msg.getContent()).append("\n");
                }
            }
            prompt.append("\n");
        }
        
        // Add current user message
        prompt.append("User: ").append(userMessage).append("\n");
        prompt.append("Assistant: ");
        
        return prompt.toString();
    }
    
    public String startNewSession() {
        String newSessionId = UUID.randomUUID().toString();
        logger.info("Generated new session ID: {}", newSessionId);
        return newSessionId;
    }
}