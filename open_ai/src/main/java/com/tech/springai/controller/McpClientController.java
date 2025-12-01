package com.tech.springai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class McpClientController {

    @Qualifier("mcpChatClient")
    private final ChatClient chatClient;

    @GetMapping("/mcp/chat")
    public ResponseEntity<String> mcpChat(@RequestHeader("username") String username, @RequestParam("message") String message) {
        String answer = chatClient.prompt()
                .user(message+ " My username is " + username)
                .call()
                .content();
        return ResponseEntity.ok(answer);
    }
}
