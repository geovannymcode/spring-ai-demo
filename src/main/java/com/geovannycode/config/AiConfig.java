package com.geovannycode.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .build();
    }

    @Bean
    ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            VectorStore vectorStore,
            ChatMemory chatMemory) {
        
        return chatClientBuilder
                .defaultAdvisors(
                    // Advisor para memoria de conversación
                    MessageChatMemoryAdvisor.builder(chatMemory).build(),
                    
                    // Advisor para RAG (Retrieval Augmented Generation)
                    QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(1).build())
                        .build())
                .build();
    }
}
