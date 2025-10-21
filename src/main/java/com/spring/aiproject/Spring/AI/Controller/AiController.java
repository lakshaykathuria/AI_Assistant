package com.spring.aiproject.Spring.AI.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.spring.aiproject.Spring.AI.Entity.Content;
import com.spring.aiproject.Spring.AI.Service.ChatService;
import com.spring.aiproject.Spring.AI.advisors.SensitiveDataAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class AiController {

    private final ChatClient ollamaChatClient;
    private final ChatClient vertexChatClient;
    private final StabilityAiImageModel stabilityAiImageModel;

    // In-memory store for conversation history. In a real-world app, this would be per-user session.
//    private final Map<String, List<Message>> chatHistories = new ConcurrentHashMap<>();
//    private final HandlerMapping resourceHandlerMapping;
    private ChatService chatService;
    private final ChatMemory chatMemory;

    public AiController(@Qualifier("GeminiChatClient") ChatClient vertexChatClient, @Qualifier("ollamaAiChatClient") ChatClient ollamaChatClient, StabilityAiImageModel stabilityAiImageModel, HandlerMapping resourceHandlerMapping, ChatService chatService, ChatMemory chatMemory) {
        this.ollamaChatClient = ollamaChatClient;
        this.vertexChatClient = vertexChatClient;
        this.stabilityAiImageModel = stabilityAiImageModel;
        this.chatService = chatService;
        this.chatMemory = chatMemory;
    }

    public record ChatRequest(String username, String message) {}

//    For Gemini Vertex

    @PostMapping("/chat")
    public ResponseEntity<List<Content>> chat(@RequestBody ChatRequest request) {


        try {

            MessageChatMemoryAdvisor messageChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory)
                    .conversationId(request.username)
                    .build();

            List<Content> vertexMessages = vertexChatClient
                    .prompt()
//                    .system("""
//                                You are a concise and accurate coding assistant.
//                                Provide clean, ready-to-use code with minimal explanation unless asked.
//                                Focus on correctness, efficiency, and best practices.
//                                """)
                    .user(request.message)
                    .advisors(new SimpleLoggerAdvisor(), new SensitiveDataAdvisor(), messageChatMemoryAdvisor)
                    .call()
                    .entity(new ParameterizedTypeReference<List<Content>>() {
                    });


            return ResponseEntity.ok(vertexMessages);

        } catch (Exception e) {
            e.printStackTrace();

            List<Content> fallback = List.of(
                    new Content("Error", "I'm unable to respond to that right now. Please try again.", "")
            );
            return ResponseEntity.ok(fallback);
        }
    }


//
//    For Ollama

//    @PostMapping("/chat")
//    public ResponseEntity<List<Content>> chat(@RequestBody ChatRequest request) {
//        String sessionId = "default-session-id";
//
//        List<Message> history = chatHistories.computeIfAbsent(sessionId, k -> new ArrayList<>());
//
//        history.add(new UserMessage(request.message()));
//
//        List<Content> botResponse = ollamaChatClient.prompt()
//                .messages(history)
//                .system("as a Java developer")
//                .call()
//                .entity(new ParameterizedTypeReference<List<Content>>(){}) ;
////                .content();
//
//        history.add(new AssistantMessage(botResponse.get(0).getContent()));
//
//        return ResponseEntity.ok(botResponse);
//    }

    @GetMapping("/image")
    public ResponseEntity<String> textToImage(@RequestParam("prompt") String prompt) {

        ImageResponse response = stabilityAiImageModel.call(
                new ImagePrompt(prompt,
                        StabilityAiImageOptions.builder()
                                .model("stable-diffusion-xl-1024-v1-0")
                                .stylePreset("comic-book")
                                .height(1024)
                                .width(1024)
                                .build())
        );
        ImageGeneration imageGen = response.getResult();

        if (imageGen.getOutput().getUrl() != null) {
            return ResponseEntity.ok(imageGen.getOutput().getUrl().toString());
        } else {
            String base64 = imageGen.getOutput().getB64Json();
            String imageUrl = "data:image/png;base64," + base64;
            return ResponseEntity.ok(imageUrl);
        }
    }

    @GetMapping("/stream-chat")
    public ResponseEntity<Flux<String>> streamChat(@RequestParam ("q") String query){
        return ResponseEntity.ok(this.chatService.streamChat(query));
    }
}