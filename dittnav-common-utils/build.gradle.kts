val kafkaVersion = "2.3.0"
val navTokenValidator = "1.1.0"
val logstashVersion = "5.2"
val logbackVersion = "1.2.3"

plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("no.nav.security:token-validation-ktor:$navTokenValidator")

}

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
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