val kafkaVersion = "2.3.0"
val navTokenValidator = "1.3.0"
val logstashVersion = "5.2"
val logbackVersion = "1.2.3"
val junitVersion = "5.4.1"
val mockkVersion = "1.9.3"
val kluentVersion = "1.52"
val jjwtVersion = "0.11.0"


plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("no.nav.security:token-validation-ktor:$navTokenValidator")
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    testRuntime("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    testRuntime("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

}

repositories {
    jcenter()
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

    withType<Test> {
        useJUnitPlatform()
    }
}