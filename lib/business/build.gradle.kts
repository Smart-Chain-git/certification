val merkleTreeVersion : String by project.extra
val flapdoodleVersion: String by project.extra
val mockKVersion: String by project.extra
val ej4tezosVersion: String by project.extra

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
    implementation("com.sword.signature:merkle-tree:$merkleTreeVersion")


    // Spring
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("org.springframework.integration:spring-integration-mongodb")

    // JWT
    implementation("com.auth0:java-jwt:3.10.2")

    // ej4tezos
    implementation("org.ej4tezos:java-se.api:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.papi:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.model:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.assert:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.osgi:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.bytes:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.default-impl:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.crypto-default-impl:$ej4tezosVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:$flapdoodleVersion")
    testImplementation("io.mockk:mockk:$mockKVersion")
}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}
