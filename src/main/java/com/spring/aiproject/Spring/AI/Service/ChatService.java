package com.spring.aiproject.Spring.AI.Service;

import com.spring.aiproject.Spring.AI.Entity.Content;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    List<Content> chat(String query, String username);

    Flux<String> streamChat(String query);
}
