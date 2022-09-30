plugins {
    `maven-publish`
    `java-library`
    kotlin("jvm")
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
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

dependencies {
    api(kotlin("stdlib-jdk8"))
    implementation(Influxdb.java)
    implementation(Kotlinx.coroutines)
    implementation(Logback.classic)
    implementation(Logstash.logbackEncoder)
    testImplementation(kotlin("test-junit5"))
    testImplementation(Junit.api)
    testImplementation(Junit.engine)
    testImplementation(Mockk.mockk)
    testImplementation(Kotest.assertionsCore)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "13"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "13"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}
