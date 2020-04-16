
plugins {
    id("kotlin")
}

description = "bean representation of commun object not linked to business or model"
val artefactName="common"
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
