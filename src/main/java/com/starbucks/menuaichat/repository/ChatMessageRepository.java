package com.starbucks.menuaichat.repository;

import com.starbucks.menuaichat.model.ChatMessage;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Long> {
    
    @Query("SELECT * FROM chat_messages WHERE session_id = :sessionId ORDER BY timestamp ASC")
    List<ChatMessage> findBySessionIdOrderByTimestamp(@Param("sessionId") Long sessionId);
}