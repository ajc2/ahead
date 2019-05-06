import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.0"
}

application {
    mainClassName = "ajc2.ahead.MainKt"
}

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    implementation("com.github.ajalt:clikt:1.7.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
