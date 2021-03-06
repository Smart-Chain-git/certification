val mockKVersion: String by project.extra
val springmockkVersion: String by project.extra
val ej4tezosVersion: String by project.extra
val jasyptVersion: String by project.extra

plugins {
    id("kotlin")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "daemon batch"
val artefactName = "daemon"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Jasypt
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:$jasyptVersion")

    // Spring
    implementation("org.mongodb:mongodb-driver-sync")
    implementation("org.springframework.integration:spring-integration-mongodb")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Project libs
    implementation(project(":lib:business"))
    implementation(project(":lib:model"))
    implementation(project(":lib:tezos-reader"))
    implementation(project(":lib:tezos-writer"))

    // ej4tezos
    implementation("org.ej4tezos:java-se.model:$ej4tezosVersion")
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
