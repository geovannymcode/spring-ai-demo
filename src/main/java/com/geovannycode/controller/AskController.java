package com.geovannycode.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ask")
@RequiredArgsConstructor
public class AskController {
    
    private final OpenAiChatModel chatModel;

    @GetMapping
    public String ask(@RequestParam String message) {
        return chatModel.call(message);
    }
}
