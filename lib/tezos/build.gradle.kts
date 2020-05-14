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

    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("org.springframework.integration:spring-integration-mongodb")

    // ej4tezos
    implementation("org.ej4tezos:java-se.api:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.papi:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.model:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.assert:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.osgi:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.bytes:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.default-impl:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.crypto-default-impl:$ej4tezosVersion")
    implementation("org.bouncycastle:bcprov-jdk15on:1.64")
    implementation("com.google:bitcoinj:0.11.3")
    implementation("org.json:json:20180813")
    implementation("org.apache.httpcomponents:httpclient:4.5.7")
    implementation("commons-io:commons-io:2.6")

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