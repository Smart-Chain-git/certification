val flapdoodleVersion: String by project.extra
val mockKVersion: String by project.extra
val ej4tezosVersion: String by project.extra
val springmockkVersion: String by project.extra

plugins {
    id("kotlin")
    id("org.springframework.boot")
    kotlin("plugin.spring")
}

description = "Business representation of application data"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Project
    api(project(":lib:common"))
    implementation(project(":lib:model"))
    implementation(project(":lib:merkle-tree"))
    implementation(project(":lib:tezos-reader"))


    // Spring
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("org.springframework.integration:spring-integration-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // JWT
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:$flapdoodleVersion")
    testImplementation("io.mockk:mockk:$mockKVersion")
    testImplementation("com.ninja-squad:springmockk:$springmockkVersion")
}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}
