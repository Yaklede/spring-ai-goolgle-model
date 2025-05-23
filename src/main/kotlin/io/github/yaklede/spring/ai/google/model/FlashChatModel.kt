package io.github.yaklede.spring.ai.google.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.metadata.*
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.model.Generation
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.web.reactive.function.client.WebClient

@Component
class FlashChatModel(
    webClientBuilder: WebClient.Builder,
    private val chatProperties: OpenAiChatProperties
) : ChatModel {

    private val webClient: WebClient = webClientBuilder
        .baseUrl(chatProperties.baseUrl)
        .build()

    override fun call(prompt: Prompt): ChatResponse {
        // 1) Prompt 지시문을 하나의 텍스트로 결합
        val combinedText = prompt.instructions
            .joinToString(separator = "\n") { it.text.replace("\n", " ") }

        // 2) 요청 바디 구성
        val requestBody: Map<String, List<Map<String, Any?>>> = mapOf(
            "contents" to listOf(
                mapOf(
                    "parts" to listOf(
                        mapOf("text" to combinedText)
                    )
                )
            )
        )


        // 3) Flash API 호출
        val resp = webClient.post()
            .uri { b ->
                b.path(chatProperties.completionsPath)
                    .queryParam("key", chatProperties.apiKey)
                    .build()
            }
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(FlashResponse::class.java)
            .block()

        Assert.state(resp != null && resp.candidates.isNotEmpty(), "Empty response from Flash API")

        // 4) 첫 번째 candidate에서 텍스트 추출
        val candidate = resp!!.candidates[0]
        val text = candidate.content.parts[0].text

        // 5) Generation 및 ChatResponseMetadata 생성
        val generation = Generation(
            AssistantMessage(text, emptyMap(), emptyList(), emptyList()),
            ChatGenerationMetadata.builder()
                .finishReason(candidate.finishReason)
                .build()
        )

        val usage: Usage = resp.usageMetadata?.let {
            DefaultUsage(it.promptTokenCount, it.candidatesTokenCount, it.totalTokenCount, null)
        } ?: EmptyUsage()

        val metadata = ChatResponseMetadata.builder()
            .model(chatProperties.options.model)
            .usage(usage)
            .keyValue("responseId", resp.responseId)
            .keyValue("modelVersion", resp.modelVersion)
            .build()

        return ChatResponse(listOf(generation), metadata)
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class FlashResponse(
        val candidates: List<Candidate>,
        val usageMetadata: UsageMetadata?,
        val modelVersion: String,
        val responseId: String
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Candidate(
            val content: Content,
            val finishReason: String,
            val citationMetadata: CitationMetadata?,
            val avgLogprobs: Double
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Content(
            val parts: List<Part>,
            val role: String
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Part(val text: String)

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class CitationMetadata(
            val citationSources: List<CitationSource>
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class CitationSource(
                val startIndex: Int,
                val endIndex: Int
            )
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class UsageMetadata(
            val promptTokenCount: Int,
            val candidatesTokenCount: Int,
            val totalTokenCount: Int,
            val promptTokensDetails: List<ModalityCount>,
            val candidatesTokensDetails: List<ModalityCount>
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class ModalityCount(
                val modality: String,
                val tokenCount: Int
            )
        }
    }
}