val kafkaVersion = "2.3.0"

plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.apache.kafka:kafka-clients:$kafkaVersion")
}

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/navikt/dittnav-common-lib")
            credentials {
                username = "x-access-token"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}