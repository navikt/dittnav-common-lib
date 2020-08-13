plugins {
    val kotlinVersion = "1.3.71"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
}

val libraryVersion = properties["lib_version"] ?: "latest-local"

subprojects {
    group = "no.nav"
    version = libraryVersion
}

repositories {
    mavenCentral()
    mavenLocal()
}

tasks {
    jar {
        enabled = false
    }
}