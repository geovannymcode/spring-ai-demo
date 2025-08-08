package com.geovannycode.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geovannycode.service.ConversationService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping
    public String createConversation() {
        return conversationService.createConversation();
    }

    @PostMapping("/{conversationId}")
    public String sendMessage(@PathVariable String conversationId, @RequestBody String message) {
        return conversationService.sendMessage(conversationId, message);
    }
}
