    package com.spring.aiproject.Spring.AI.Config;

    import org.springframework.ai.chat.client.ChatClient;
    import org.springframework.ai.chat.memory.ChatMemory;
    import org.springframework.ai.chat.memory.ChatMemoryRepository;
    import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
    import org.springframework.ai.chat.memory.MessageWindowChatMemory;
    import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
    import org.springframework.ai.ollama.OllamaChatModel;
    import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class AiModelConfig {

        @Bean(name = "ollamaAiChatClient")
        public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel){
            return ChatClient.builder(ollamaChatModel).build();
        }

        @Bean(name = "GeminiChatClient")
        public ChatClient geminiChatClient(VertexAiGeminiChatModel geminiChatModel) {
            return ChatClient.builder(geminiChatModel).build();
        }

//        @Bean
//        public ChatMemoryRepository chatMemoryRepository() {
//            return new InMemoryChatMemoryRepository();
//        }


        @Autowired
        JdbcChatMemoryRepository chatMemoryRepository;

        @Bean
        public ChatMemory chatMemory() {
            return MessageWindowChatMemory.builder()
                    .chatMemoryRepository(chatMemoryRepository)
                    .maxMessages(20)
                    .build();
        }

    }
