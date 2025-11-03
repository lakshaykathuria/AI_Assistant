package com.spring.aiproject.Spring.AI.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component

public class DateTimeTool {

    @Tool(description = "Get the current date and time in users zone.")
    public String getCurrentDateTime(){
        log.info("Tool is calling");

        var datetime = LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();

        log.info(datetime);

        return LocalDateTime.now()
                .atZone(LocaleContextHolder.getTimeZone().toZoneId())
                .toString();

    }
}
