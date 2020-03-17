plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "Model representation of database data"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // MongoDB
    api("org.springframework.boot:spring-boot-starter-data-mongodb")
}

tasks {
    jar {
        enabled=true
    }
    bootJar {
        enabled=false
    }
}
