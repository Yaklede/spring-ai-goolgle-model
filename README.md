# spring-ai-google-model

<!-- 기본 한국어로 표시 -->

<details open>
<summary>🇰🇷 한국어 (기본)</summary>

# spring-ai-google-model

Google Gemini Flash 2.0을 OpenAI 스타일 API 호출만으로 사용할 수 있도록 해주는 라이브러리입니다. **Vertex AI** 의존성이 필요 없으며, 기존 `spring.ai.openai` 설정을 그대로 재활용할 수 있습니다.

## 주요 특징

* **Vertex AI 불필요**: 무료 Flash 2.0 API 직접 호출
* **설정 재사용**: `spring.ai.openai` 프로퍼티 활용
* **Spring Component**: `@Component` 자동 빈 등록
* **WebClient 기반**: 비동기 REST 호출 지원

## 빠른 시작

### 1. Gradle 의존성

```groovy
dependencies {
    implementation("io.github.yaklede:spring.ai.google.model:0.1.2")
}
```

### 2. application.yml 설정

```yaml
spring:
  ai:
    openai:
      api-key: YOUR_GOOGLE_API_KEY
      chat:
        base-url: https://generativelanguage.googleapis.com
        completions-path: /v1beta/models/gemini-2.0-flash:generateContent
        api-key: YOUR_GOOGLE_API_KEY
```

### 3. 라이브러리 사용 방법

`spring-ai-google-model` 라이브러리에 이미 포함된 `FlashChatModel`과 `DefaultChatClientBuilder`를 활용하여, 별도의 모델 선언 없이 바로 사용할 수 있습니다.

```kotlin
@Service
class ChatService(private val flashChatModel: FlashChatModel) {
    fun askGemini(question: String) {
        val client = DefaultChatClientBuilder(
            flashChatModel,
            ObservationRegistry.NOOP,
            null
        ).build()

        val reply = client.prompt(question)
            .call()
            .content()

        println("→ Gemini 응답: $reply")
    }
}
```

---

### 4. 서비스 연동 예시

. 서비스 연동 예시

```kotlin
@Service
class ChatService(private val flashChatModel: FlashChatModel) {
    fun askGemini(question: String) {
        val client = DefaultChatClientBuilder(flashChatModel, ObservationRegistry.NOOP, null).build()
        val reply = client.prompt(question).call().content()
        println("→ Gemini 응답: $reply")
    }
}
```

## 배경 및 동기

기존 Spring AI 모듈은 Google Gemini Flash 2.0을 사용하려면 Vertex AI SDK와 GCP 인증 설정이 필요해, 테스트 비용과 초기 설정 부담이 컸습니다. Flash API 자체 호출은 무료이므로, **OpenAI API 호출 패턴을 재사용**하는 방식으로 간단히 해결하였습니다.

## 기여

* GitHub: [https://github.com/Yaklede/spring-ai-goolgle-model](https://github.com/Yaklede/spring-ai-goolgle-model)
* 이슈/PR 환영! ⭐ Fork와 Star 부탁드립니다.

</details>

<details>
<summary>🇺🇸 English</summary>

# spring-ai-google-model

Use Google Gemini Flash 2.0 in Spring AI without a paid Vertex AI dependency by leveraging familiar OpenAI-style API calls.

## Key Features

* **No Vertex AI required**: Call the free Flash 2.0 API directly
* **Reuse OpenAI configs**: Keep using your existing `spring.ai.openai` properties
* **Spring Component**: `@Component` and `FlashChatModel` provided by the library
* **WebClient-based**: Support for asynchronous REST calls

## Quickstart

### 1. Add Gradle Dependency

```groovy
dependencies {
    implementation("io.github.yaklede:spring.ai.google.model:0.1.2")
}
```

### 2. Configure application.yml

```yaml
spring:
  ai:
    openai:
      api-key: YOUR_GOOGLE_API_KEY
      chat:
        base-url: https://generativelanguage.googleapis.com
        completions-path: /v1beta/models/gemini-2.0-flash:generateContent
        api-key: YOUR_GOOGLE_API_KEY
```

### 3. Use in Your Service

The library includes `FlashChatModel` and `DefaultChatClientBuilder`. Simply inject and call:

```kotlin
@Service
class ChatService(private val flashChatModel: FlashChatModel) {
    fun askGemini(question: String) {
        val client = DefaultChatClientBuilder(
            flashChatModel,
            ObservationRegistry.NOOP,
            null
        ).build()

        val reply = client.prompt(question)
            .call()
            .content()

        println("→ Gemini says: $reply")
    }
}
```

## Background and Motivation

The official Spring AI module requires the Vertex AI SDK and GCP setup to use Google Gemini Flash 2.0, incurring costs and extra configuration. Since the Flash 2.0 API itself is free, this library reuses the OpenAI call pattern to simplify integration without any Vertex AI dependency.

## Contributing

* GitHub: [https://github.com/Yaklede/spring-ai-goolgle-model](https://github.com/Yaklede/spring-ai-goolgle-model)
* Issues and PRs welcome! ⭐ Please fork and star the repo.

</details>
