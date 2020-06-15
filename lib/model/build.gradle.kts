plugins {
    id("kotlin")
    kotlin("kapt")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

description = "Model representation of database data"

dependencies {


    //querydsl
    api("com.querydsl:querydsl-mongodb")
    kapt("com.querydsl:querydsl-apt")


    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    api(project(":lib:common"))
    implementation(project(":lib:merkle-tree"))

    // MongoDB
    api("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
}
kapt {
    annotationProcessor("org.springframework.data.mongodb.repository.support.MongoAnnotationProcessor")
}
tasks {
    jar {
        enabled = true
    }
    bootJar {
        enabled = false
    }
}
