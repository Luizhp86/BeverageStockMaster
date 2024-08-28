package com.example.BeverageStockMaster.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService {
    private final OpenAiChatModel openAiChatModel;
    private final Logger logger = LoggerFactory.getLogger(OpenAiService.class);


    public OpenAiService(OpenAiChatModel openAiChatModel) {
        this.openAiChatModel = openAiChatModel;
    }

    public String openAiChat(String messageRequest) {


        Prompt prompt = new Prompt(
                messageRequest,
                OpenAiChatOptions.builder()
                        .withModel("gpt-3.5-turbo")
                        .withTemperature(0.4F)
                        .build()
        );

        ChatResponse response = openAiChatModel.call(prompt);

        logger.info("Resposta: {}", response.getResult().getOutput().getContent());
        return response.getResult().getOutput().getContent();
    }

}
