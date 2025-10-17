package com.example.chatapi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OpenAIConfigValidator implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${openai.api.key}")
    private String apiKey;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Validating OpenAI API configuration...");

        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equals("your-openai-api-key-here")) {
            log.error("================================================================");
            log.error("HATA: OpenAI API key yapılandırılmamış!");
            log.error("Lütfen OPENAI_API_KEY environment variable'ını ayarlayın.");
            log.error("Örnek: export OPENAI_API_KEY=sk-your-key-here");
            log.error("================================================================");

            // Uygulamayı kapat
            System.exit(1);
        }

        log.info("OpenAI API key validated successfully");
    }
}
