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
}
