plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "serveur ihm"
val artefactName="web"
dependencies {

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-rsocket")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    implementation("org.springframework.boot:spring-boot-starter-webflux")


    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("org.springframework.security:spring-security-rsocket")


    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")

    implementation(project(":lib:api"))
    implementation(project(":lib:business"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-jwt:1.1.0.RELEASE")

    // JWT
    implementation("com.auth0:java-jwt:3.10.2")

    runtimeOnly("org.webjars:webjars-locator-core")
    runtimeOnly("org.webjars:bootstrap:4.4.1-1")
    runtimeOnly("org.webjars:jquery:3.4.1")
    runtimeOnly("org.webjars.npm:bootstrap-table:1.16.0")
    runtimeOnly("org.webjars:bootstrap-notify:3.1.3-1")


    compileOnly("org.springframework.boot:spring-boot-devtools")

    val mockKVersion: String by project.extra
    testImplementation("io.mockk:mockk:$mockKVersion")
    val springmockkVersion: String by project.extra
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

