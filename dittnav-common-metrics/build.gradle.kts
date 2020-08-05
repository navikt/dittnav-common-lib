val logstashVersion = "5.2"
val logbackVersion = "1.2.3"
val influxdbVersion = "2.8"
val coroutinesVersion = "1.3.7"

plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
}

repositories {
    mavenCentral()
    mavenLocal()
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

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("ch.qos.logback:logback-classic:$logbackVersion")
    compile("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    compile("org.influxdb:influxdb-java:$influxdbVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}