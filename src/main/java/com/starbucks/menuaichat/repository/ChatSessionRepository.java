package com.starbucks.menuaichat.repository;

import com.starbucks.menuaichat.model.ChatSession;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatSessionRepository extends CrudRepository<ChatSession, Long> {
    
    @Query("SELECT * FROM chat_sessions WHERE session_id = :sessionId")
    Optional<ChatSession> findBySessionId(@Param("sessionId") String sessionId);
}