import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
	// Dokka
	id("org.jetbrains.dokka") version "1.7.20"
	// Plugin de Serialization Kotlin
	kotlin("plugin.serialization") version "1.7.20"
}

group = "resa.mario"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot & Spring Data Reactive
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

	// Spring Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// Spring Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// WebFlux Reactive
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	// Websocket
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	// Dependencias Kotlin
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// Base de datos
	//runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:r2dbc-postgresql")
	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test"){
		exclude(module = "mockito-core") // Desactivamos Mockito
	}
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.security:spring-security-test")

	// Mockk
	testImplementation("com.ninja-squad:springmockk:4.0.2")

	// Corrutinas en test
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

	// Logs
	implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

	// Result
	implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.17")

	// Dokka
	dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.8.10")

	// JWT
	implementation("com.auth0:java-jwt:4.3.0")

	// Serialization JSON
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

	// Swagger-SpringDoc-OpenAPI
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

