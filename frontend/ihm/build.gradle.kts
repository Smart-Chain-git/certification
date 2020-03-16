plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "serveur ihm"
dependencies {

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    val mockKVersion: String by project.extra
    testImplementation("io.mockk:mockk:$mockKVersion")
    val springmockkVersion: String by project.extra
    testImplementation("com.ninja-squad:springmockk:$springmockkVersion")

}


springBoot {
    buildInfo()
}

tasks {
    bootJar {
        archiveBaseName.set("signature-ihm")
    }
}

