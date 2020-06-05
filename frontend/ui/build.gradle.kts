val mockKVersion: String by project.extra
val springmockkVersion: String by project.extra

plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "ui web server"
val artefactName = "ui"

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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-messaging")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.data:spring-data-commons")

    // Project libs
    implementation(project(":lib:api"))
    implementation(project(":lib:business"))
    implementation(project(":lib:web-core"))

    // Devtools
    implementation("org.springframework.boot:spring-boot-devtools")

    // Runtime
    runtimeOnly("org.webjars:webjars-locator-core")
    runtimeOnly("org.webjars:bootstrap:4.4.1-1")
    runtimeOnly("org.webjars:jquery:3.4.1")
    runtimeOnly("org.webjars.npm:bootstrap-table:1.16.0")
    runtimeOnly("org.webjars.npm:bs-custom-file-input:1.3.4")
    runtimeOnly("org.webjars:bootstrap-notify:3.1.3-1")
    runtimeOnly("org.webjars:font-awesome:5.13.0")
    runtimeOnly("org.webjars.bower:crypto-js:3.1.9-1")

    // Test
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

    val unpack by registering(Copy::class) {
        dependsOn("bootJar")
        from(zipTree("build/libs/$artefactName-$version.jar"))
        into("build/unpacked")
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
