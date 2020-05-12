val mockKVersion: String by project.extra
val springmockkVersion: String by project.extra

plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "rsocket api server"
val artefactName = "rsocket"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("org.springframework.security:spring-security-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Project libs
    implementation(project(":lib:api"))
    implementation(project(":lib:business"))
    implementation(project(":lib:web-core"))

}


springBoot {
    buildInfo()
}

tasks {
    bootJar {
        archiveBaseName.set(artefactName)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = artefactName
            artifact(tasks["bootJar"])
        }
    }
}
