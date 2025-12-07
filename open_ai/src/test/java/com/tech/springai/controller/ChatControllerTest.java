package com.tech.springai.controller;

import org.junit.jupiter.api.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = {
        "spring.ai.openai.api-key=${OPENAI_API_KEY:test-key}",
        "logging.level.org.springframework.ai=DEBUG"
})
@WebMvcTest(ChatController.class)
@Import(ChatControllerTest.ChatEvaluatorConfig.class)
@Disabled
class ChatControllerTest {

    @Autowired
    private ChatController chatController;

    @Autowired
    private RelevancyEvaluator relevancyEvaluator;

    @Value("${test.relevancy.min-score:0.7}")
    private float minRelevancyScore;

//    ChatControllerTest(ChatController chatController,
//                       RelevancyEvaluator relevancyEvaluator) {
//        this.chatController = chatController;
//        this.relevancyEvaluator = relevancyEvaluator;
//    }

    @Test
    @DisplayName("Should return relevant response for basic geography question")
    @Timeout(value = 30)
    void evaluateChatControllerResponseRelevancy() {
        // Given
        String question = "What is the capital of India";

        // When
        String aiResponse = chatController.chat(question);
        EvaluationRequest evaluationRequest = new EvaluationRequest(question, aiResponse);
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        // Then
        Assertions.assertAll(() -> assertThat(aiResponse).isNotBlank(),
                () -> assertThat(evaluationResponse.isPass())
                        .withFailMessage("""
                                ========================================
                                The answer was not considered relevant.
                                Question: "%s"
                                Response: "%s
                                ========================================
                                """, question, aiResponse)
                        .isTrue(),
                () -> assertThat(evaluationResponse.getScore())
                        .withFailMessage("""
                                ======================================================
                                The score %.2f is lower than the minimum required %.2f.
                                Question: "%s"
                                Response: "%s"
                                =======================================================
                                """, evaluationResponse.getScore(), minRelevancyScore, question, aiResponse)
                        .isGreaterThan(minRelevancyScore));

    }

    @Import(ChatController.class)
    static class ChatEvaluatorConfig {

        @MockitoBean
        private ChatClient chatClient;

        @MockitoBean
        private ChatModel chatModel;

        @org.springframework.context.annotation.Bean
        RelevancyEvaluator relevancyEvaluator() {

            ChatClient.Builder builder =
                    ChatClient.builder(chatModel)
                            .defaultAdvisors(new SimpleLoggerAdvisor());

            return new RelevancyEvaluator(builder);
        }
    }
}
