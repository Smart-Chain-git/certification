val mongockVersion: String by project.extra

plugins {
    id("kotlin")
}

description = "bean representation of communication api between client and server"
val artefactName = "communication-api"
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

tasks {
    jar {
        enabled = true
    }
}
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = artefactName
            artifact(tasks["jar"])
            artifact(tasks["sourcesJar"])
        }
    }
}
