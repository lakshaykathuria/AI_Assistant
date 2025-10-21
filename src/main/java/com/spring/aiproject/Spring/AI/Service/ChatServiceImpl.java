package com.spring.aiproject.Spring.AI.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatServiceImpl implements ChatService{

    private ChatClient chatClient;

    public ChatServiceImpl(ChatClient GeminiChatClient) {
        this.chatClient = GeminiChatClient;
    }

    @Override
    public String chat(String query) {
        return "";
    }

    @Override
    public Flux<String> streamChat(String query) {
        return this.chatClient
                .prompt()
                .system("You are a helpful assistant.")
                .user(u -> u.text("Tell me about {topic}").param("topic", query))
                .stream()
                .content();
    }
}
