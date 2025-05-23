package io.github.yaklede.spring.ai.google.model

import io.micrometer.observation.ObservationRegistry
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.DefaultChatClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FlashChatModelTest @Autowired constructor(
    private val flashChatModel: FlashChatModel
) {
    @Test
    fun modelCallTest() {
        val client = DefaultChatClientBuilder(
            flashChatModel,
            ObservationRegistry.NOOP,
            null
        ).build()

        val response = client.prompt("Tell me about Gemini ai")
            .call()
            .content()

        println(response)
    }
}