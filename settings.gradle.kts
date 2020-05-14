pluginManagement {
    val mavenProxyUrl: String by extra
    val kotlinVersion: String by extra

    val dependencyManagementPluginVersion: String by extra
    val sonarqubeVersion: String by extra
    val springBootVersion: String by extra
    plugins {


        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version dependencyManagementPluginVersion
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("org.sonarqube") version sonarqubeVersion
    }
    repositories {
        mavenLocal()
        maven { url = uri(mavenProxyUrl); isAllowInsecureProtocol = true }
        maven { url = uri("https://repo.spring.io/snapshot") }
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        gradlePluginPortal()
    }
}
rootProject.name = "com.sword.signature.servers"


include(":lib:common")
include(":lib:business")
include (":lib:model")
include (":lib:api")
include (":lib:web-core")
include(":lib:tezos")

include (":backend:daemon")

include (":frontend:rest")
include (":frontend:rsocket")
include (":frontend:ui")
