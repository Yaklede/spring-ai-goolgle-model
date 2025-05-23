import com.vanniktech.maven.publish.SonatypeHost

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.Yakelde"
version = "0.1.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springAiVersion"] = "1.0.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.ai:spring-ai-starter-model-openai")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val sourcesJar by tasks.creating(Jar::class) {
	archiveClassifier.set("sources")
	from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
	archiveClassifier.set("javadoc")
	// Dokka를 쓰신다면 dokkaHtml 출력 디렉토리로 대체하세요.
	from(tasks.named("javadoc"))
}

mavenPublishing {
	coordinates( // Coordinate(GAV)
		groupId = "io.github.yaklede",
		artifactId = "spring.ai.google.model",
		version = "0.1.0"
	)

	pom {
		name.set("Spring AI FlashChatModel client") // Project Name
		description.set("falsh Chat model") // Project Description
		inceptionYear.set("2025") // 개시년도
		url.set("https://github.com/Yaklede/spring-ai-goolgle-model") // Project URL

		licenses { // License Information
			license {
				name.set("The Apache License, Version 2.0")
				url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
			}
		}

		developers { // Developer Information
			developer {
				id.set("Yaklede")
				name.set("Jimin Jo")
				email.set("chh78151@gmail.com")
			}
		}

		scm { // SCM Information
			connection.set("scm:git:git://github.com/Yaklede/spring-ai-goolgle-model.git")
			developerConnection.set("scm:git:ssh://github.com/Yaklede/spring-ai-goolgle-model.git")
			url.set("https://github.com/Yaklede/spring-ai-goolgle-model.git")
		}
	}

	publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

	signAllPublications() // GPG/PGP 서명
}
