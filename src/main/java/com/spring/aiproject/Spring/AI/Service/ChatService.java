package com.spring.aiproject.Spring.AI.Service;

import reactor.core.publisher.Flux;

public interface ChatService {

    String chat(String query);

    Flux<String> streamChat(String query);
}
