val mongockVersion: String by project.extra

plugins {
    id("kotlin")
    kotlin("plugin.spring")
}

description = "object commun au différent serice web (ui,rest,rsocket)"
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Project libs
    implementation(project(":lib:api"))
    implementation(project(":lib:business"))

}

tasks {
    jar {
        enabled = true
    }
}


