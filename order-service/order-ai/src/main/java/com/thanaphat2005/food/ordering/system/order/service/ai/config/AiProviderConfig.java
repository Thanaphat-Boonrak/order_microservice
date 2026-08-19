package com.thanaphat2005.food.ordering.system.order.service.ai.config;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiProviderConfig {

    @Bean("geminiChatClient")
    ChatClient geminiChatClient(GoogleGenAiChatModel chatModel,SimpleLoggerAdvisor loggerAdvisor) {
        return ChatClient.builder(chatModel).defaultAdvisors(loggerAdvisor).build();
    }


    @Bean
    SimpleLoggerAdvisor simpleLoggerAdvisor(){
        return new SimpleLoggerAdvisor();
    }
}
