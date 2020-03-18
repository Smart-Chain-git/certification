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
    // Project
    implementation(project(":lib:model"))
    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}
