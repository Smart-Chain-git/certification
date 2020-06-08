val flapdoodleVersion: String by project.extra
val mockKVersion: String by project.extra
val ej4tezosVersion: String by project.extra

plugins {
    id("kotlin")
    id("org.springframework.boot")
    kotlin("plugin.spring")
}

description = "Tezos connectors implementation"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}