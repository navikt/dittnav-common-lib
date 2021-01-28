plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
}

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    implementation(Kotlinx.coroutines)
    testImplementation(kotlin("test-junit5"))
    testImplementation(Junit.engine)
    testImplementation(Kluent.kluent)
    testImplementation(Mockk.mockk)
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://packages.confluent.io/maven")
    maven("https://jitpack.io")
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