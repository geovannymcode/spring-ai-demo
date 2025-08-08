package com.geovannycode.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ChatClient chatClient;
    private final Map<String, String> conversations = new HashMap<>();

    public String createConversation() {
        String conversationId = UUID.randomUUID().toString();
        log.info("Creando nueva conversación con ID: {}", conversationId);
        
        conversations.put(conversationId, "Nueva conversación");
        
        return conversationId;
    }

    public String sendMessage(String conversationId, String userMessage) {
        if (!conversations.containsKey(conversationId)) {
            log.warn("Intento de acceder a conversación inexistente: {}", conversationId);
            throw new IllegalArgumentException("Conversación no encontrada");
        }

        log.info("Mensaje recibido para conversación {}: {}", conversationId, userMessage);
        
        // Realizar la llamada al ChatClient con el ID de conversación
        String response = chatClient.prompt()
                .user(userMessage)
                .advisors(advisorSpec -> advisorSpec
                        .param(CONVERSATION_ID, conversationId))
                .call()
                .content();
        
        log.info("Respuesta generada para conversación {}", conversationId);
        
        return response;
    }
    
    public String askWithContext(String conversationId, String userMessage, String context) {
        if (!conversations.containsKey(conversationId)) {
            log.warn("Intento de acceder a conversación inexistente: {}", conversationId);
            throw new IllegalArgumentException("Conversación no encontrada");
        }

        log.info("Pregunta con contexto para conversación {}: {}", conversationId, userMessage);
        
        // Realizar la llamada al ChatClient con filtro de contexto
        String response = chatClient.prompt()
                .user(userMessage)
                .advisors(advisorSpec -> advisorSpec
                        .param(CONVERSATION_ID, conversationId)
                        .param(QuestionAnswerAdvisor.FILTER_EXPRESSION, context))
                .call()
                .content();
        
        log.info("Respuesta con contexto generada para conversación {}", conversationId);
        
        return response;
    }
}
