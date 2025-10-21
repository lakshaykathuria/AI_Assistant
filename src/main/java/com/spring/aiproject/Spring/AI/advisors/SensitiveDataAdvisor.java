package com.spring.aiproject.Spring.AI.advisors;

import com.spring.aiproject.Spring.AI.Entity.Content;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;


@Slf4j
public class SensitiveDataAdvisor implements CallAdvisor {

    private static final Pattern sensitiveWordsPattern = Pattern.compile(
            "\\b(password|ssn|creditcard|pan|aadhaar|sex|sexy)\\b",
            Pattern.CASE_INSENSITIVE
    );


    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        boolean containsSensitive = request.prompt().getInstructions().stream()
                .filter(m -> m instanceof UserMessage)
                .map(m -> ((UserMessage) m).getText())
                .anyMatch(text -> sensitiveWordsPattern.matcher(text).find());

        if (containsSensitive) {
            log.warn("⚠️ Request blocked by SensitiveDataAdvisor");

            try {
                List<Content> blockedList = List.of(
                        new Content("GUARDED", "Security Warning", "⚠️ Your request contains sensitive data and was blocked.")
                );

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(blockedList);

                AssistantMessage assistantMessage = new AssistantMessage(json);

                ChatResponse chatResponse = new ChatResponse(List.of(new Generation(assistantMessage)));

                return new ChatClientResponse(chatResponse, Collections.emptyMap());

            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize blocked message", e);
            }
        }

        return chain.nextCall(request);
    }


    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}

