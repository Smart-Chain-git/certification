plugins {
    id("kotlin")
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    id("jacoco")
    id("org.sonarqube")
    id("org.jetbrains.dokka") version "0.10.1"
}

val artifactName = "merkle-tree"
description = "Merkle tree library"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.slf4j:slf4j-api")

    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("ch.qos.logback:logback-classic")
}


tasks {
    val sourcesJar by registering(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val dokka by getting(org.jetbrains.dokka.gradle.DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokka"
    }

    jar {
        enabled = true
    }
}

publishing {
    publications {
        val maven by creating(MavenPublication::class) {
            artifactId = artifactName
            artifact(tasks["sourcesJar"])
        }
    }
}
