val mockKVersion: String by project.extra
val springmockkVersion: String by project.extra

plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.1.0"
}

description = "Rest api server"
val artefactName = "rest"

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
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    //documentation
    val springdocVersion : String by project.extra
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-security:$springdocVersion")

    // Project libs
    implementation(project(":lib:api"))
    implementation(project(":lib:business"))
    implementation(project(":lib:web-core"))

    implementation("org.springframework.boot:spring-boot-devtools")

}


springBoot {
    buildInfo()
}





tasks {

    processResources {
        from("../jsclient/build/dist") {
            into("public-web-resources")
        }
    }

    bootJar {
        dependsOn(":frontend:jsclient:build")
        archiveBaseName.set(artefactName)
    }

    val unpack by registering(Copy::class) {
        dependsOn("bootJar")
        from(zipTree("build/libs/$artefactName-$version.jar"))
        into("build/unpacked")
    }
}

openApi {
    apiDocsUrl.set("http://localhost:9090/v3/api-docs")
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = artefactName
            artifact(tasks["bootJar"])
        }
    }
}
