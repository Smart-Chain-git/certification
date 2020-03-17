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
}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}
