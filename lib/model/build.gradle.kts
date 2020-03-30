val merkleTreeVersion: String by project.extra
val mongockVersion: String by project.extra

plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "Model representation of database data"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // MongoDB
    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    // Merkle tree library
    api("com.sword.signature:merkle-tree:$merkleTreeVersion")
    // Mongock
    // implementation("com.github.cloudyrock.mongock:mongock-spring:$mongockVersion")
}

tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}
