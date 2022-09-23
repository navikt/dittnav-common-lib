import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    jcenter()
    maven("https://jitpack.io")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "13"
}
