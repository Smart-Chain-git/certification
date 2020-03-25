val mongockVersion: String by project.extra

plugins {
    id("kotlin")
}

description = "bean representation of communication api between client and server"
val artefactName="communication-api"
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

}

tasks {
    jar {
        enabled=true
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = artefactName
            artifact(tasks["jar"])
        }
    }
}