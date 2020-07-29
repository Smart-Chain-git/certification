plugins {
    id("io.spring.dependency-management")
    kotlin("jvm")
    id("jacoco")
    id("org.sonarqube")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

description = "Signature application using Tezos blockchain."

allprojects {
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "jacoco")
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    group = "com.sword.signature"
    version = "0.0.1-SNAPSHOT"

    repositories {
        val mavenProxyUrl: String by extra
        val ej4tezosArtifactoryUrl: String by extra

        mavenLocal()
        maven { url = uri(ej4tezosArtifactoryUrl) }
        maven { url = uri(mavenProxyUrl); isAllowInsecureProtocol = true }
        maven { url = uri("https://repo.spring.io/snapshot") }
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        gradlePluginPortal()
    }

    tasks {
        withType<Test> {
            useJUnitPlatform()
        }

        withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "11"
            }
        }

        withType<JacocoReport> {
            reports {
                // Require by sonarqube because it does not read .exec jacoco files but just .xml files.
                xml.isEnabled = true
            }
        }
    }

    jacoco {
        val jacocoToolVersion: String by extra
        toolVersion = jacocoToolVersion
    }

    dependencyManagement {
        val springBootVersion: String by extra
        val kotlinVersion: String by extra

        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion") {
                // alignement des versions de kotlins
                bomProperty("kotlin.version", kotlinVersion)
            }
        }
    }

    publishing {
        val nexusUrl: String by project.extra
        val nexusUser: String by project.extra
        val nexusPassword: String by project.extra
        val releasesRepo: String by project.extra
        val snapshotsRepo: String by project.extra

        repositories {
            maven {
                credentials {
                    username = nexusUser
                    password = nexusPassword
                }

                val releasesRepoUrl = "${nexusUrl}/repository/${releasesRepo}"
                val snapshotsRepoUrl = "${nexusUrl}/repository/${snapshotsRepo}"
                url = if (version.toString().endsWith("SNAPSHOT")) {
                    uri(snapshotsRepoUrl)
                } else {
                    uri(releasesRepoUrl)
                }
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
    val jacocoRootReport by registering(JacocoReport::class) {
        dependsOn(subprojects.map { it.tasks.withType<Test>() })
        dependsOn(subprojects.map { it.tasks.withType<JacocoReport>() })
        additionalSourceDirs.setFrom(subprojects.map { it.the<SourceSetContainer>()["main"].allSource.srcDirs })
        sourceDirectories.setFrom(subprojects.map { it.project.the<SourceSetContainer>()["main"].allSource.srcDirs })
        classDirectories.setFrom(subprojects.map { it.project.the<SourceSetContainer>()["main"].output })
        executionData.setFrom(project.fileTree(".") {
            include("**/build/jacoco/test.exec")
        })
        reports {
            xml.isEnabled = true
            csv.isEnabled = false
            html.isEnabled = true
        }
    }
}
