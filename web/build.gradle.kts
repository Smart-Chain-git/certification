val mockKVersion: String by project.extra
val springmockkVersion: String by project.extra

plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "Web server"
val artefactName = "web"

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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("org.springframework.security:spring-security-rsocket")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // Project libs
    implementation(project(":lib:api"))
    implementation(project(":lib:business"))

    runtimeOnly("org.webjars:webjars-locator-core")
    runtimeOnly("org.webjars:bootstrap:4.4.1-1")
    runtimeOnly("org.webjars:jquery:3.4.1")
    runtimeOnly("org.webjars.npm:bootstrap-table:1.16.0")
    runtimeOnly("org.webjars:bootstrap-notify:3.1.3-1")
    runtimeOnly("org.webjars:font-awesome:5.13.0")

    implementation("org.springframework.boot:spring-boot-devtools")


    testImplementation("io.mockk:mockk:$mockKVersion")
    testImplementation("com.ninja-squad:springmockk:$springmockkVersion")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
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
