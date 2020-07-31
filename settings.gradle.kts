pluginManagement {
    plugins {
        val springBootVersion: String by settings
        val kotlinVersion: String by settings
        val dependencyManagementPluginVersion: String by settings
        val sonarqubeVersion: String by settings
        val nodeGradleVersion: String by settings
        val dokkaPluginVersion: String by settings

        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementPluginVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        kotlin("plugin.noarg") version kotlinVersion
        id("com.github.node-gradle.node") version nodeGradleVersion
        id("org.sonarqube") version sonarqubeVersion
        id("org.jetbrains.dokka") version dokkaPluginVersion
    }

    repositories {
        val mavenProxyUrl: String by extra

        mavenLocal()
        maven { url = uri(mavenProxyUrl); isAllowInsecureProtocol = true }
        maven { url = uri("https://repo.spring.io/snapshot") }
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        gradlePluginPortal()
    }
}

rootProject.name = "com.sword.signature.servers"

// Librairies
include(":lib:common")
include(":lib:business")
include (":lib:model")
include (":lib:api")
include (":lib:web-core")
include(":lib:tezos-reader")
include(":lib:tezos-writer")
include(":lib:merkle-tree")
// Microservices
include (":backend:daemon")
include (":frontend:jsclient")
include (":frontend:rest")
include (":frontend:rsocket")
