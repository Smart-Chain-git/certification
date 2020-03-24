val mongockVersion: String by project.extra

plugins {
    id("kotlin")
}

description = "Model representation of database data"
val artefactName="signature-api"
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
