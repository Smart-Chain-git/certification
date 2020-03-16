import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {

    val springBootVersion: String by extra
    val kotlinVersion: String by extra
    val openApiGeneratorVersion: String by extra

    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("org.openapitools:openapi-generator-gradle-plugin:$openApiGeneratorVersion")
    }
}

plugins {
    id("io.spring.dependency-management")
    kotlin("jvm")
    id("jacoco")
    id("org.sonarqube")
}

group = "com.sword.signature"
version = "0.0.1-SNAPSHOT"
description = "application de signature vie blockchain"
allprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")

    val mavenProxyUrl: String by extra

    repositories {
        mavenLocal()
        maven { url = uri(mavenProxyUrl) ; isAllowInsecureProtocol=true}
        maven { url = uri("https://repo.spring.io/snapshot") }
        maven { url = uri("https://repo.spring.io/milestone") }
        maven{ url=uri("https://oss.sonatype.org/content/repositories/snapshots/")}
        gradlePluginPortal()
    }


    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<JacocoReport> {
        reports {
            //necessaire car pour lr moment sonarcube ne lit pas les .exec de jacoco mais juste le xml
            xml.isEnabled = true
        }
    }

    jacoco {
        val jacocoToolVersion: String by extra
        toolVersion = jacocoToolVersion
    }

    val springBootVersion: String by extra
    val kotlinVersion: String by extra

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion") {
                // alignement des versions de kotlins
                bomProperty("kotlin.version", kotlinVersion)
            }
        }
    }
}

sonarqube {
    properties {
        property("sonar.jacoco.reportPaths", "$projectDir/build/jacoco/jacocoMerge.exec")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.java.binaries", "$projectDir/build/classes/kotlin")

    }
}


tasks {
    val jacocoMerge by creating(JacocoMerge::class) {
        executionData = fileTree(project.projectDir) {
            include("**/*.exec")
            include("**/*.ec")
        }
        doFirst {
            executionData = files(executionData.filter { it.exists() })
        }
    }

    val jacocoTestReport = this.getByName("jacocoTestReport")
    jacocoTestReport.dependsOn(subprojects.map { it.tasks["jacocoTestReport"] })
    jacocoMerge.dependsOn(jacocoTestReport)

    val jacocoRootReport by creating(JacocoReport::class) {
        description = "Generates an aggregate report from all subprojects"
        group = "Coverage reports"
        dependsOn(jacocoMerge)
        sourceDirectories.setFrom(files(subprojects.flatMap { it.sourceSets["main"].allSource.srcDirs.filter { it.exists() } } ))
        classDirectories.setFrom(files(subprojects.flatMap { it.sourceSets["main"].output } ))
        executionData(jacocoMerge.destinationFile)
        reports {
            html.isEnabled = true
            xml.isEnabled = true
        }
    }
}
