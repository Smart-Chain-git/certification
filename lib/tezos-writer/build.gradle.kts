val flapdoodleVersion: String by project.extra
val mockKVersion: String by project.extra
val ej4tezosVersion: String by project.extra
val bouncyCastleVersion: String by project.extra

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

    // ej4tezos
    implementation("org.ej4tezos:java-se.api:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.papi:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.model:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.assert:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.osgi:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.utils.bytes:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.default-impl:$ej4tezosVersion")
    implementation("org.ej4tezos:java-se.crypto-default-impl:$ej4tezosVersion")
    implementation("org.bouncycastle:bcprov-jdk15on:$bouncyCastleVersion")
    implementation("com.google:bitcoinj:0.11.3")
    implementation("org.json:json:20180813")
    implementation("org.apache.httpcomponents:httpclient:4.5.7")
    implementation("commons-io:commons-io:2.6")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}