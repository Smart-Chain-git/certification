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
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
    implementation("org.springframework.boot:spring-boot-starter-security")



    implementation("org.springframework.boot:spring-boot-starter-actuator")


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

}


springBoot {
    buildInfo()
}

tasks {
    bootJar {
        archiveBaseName.set("signature-ihm")
    }
}

