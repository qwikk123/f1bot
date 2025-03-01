plugins {
    kotlin("jvm") version "1.9.0"
    id("com.google.cloud.tools.jib") version("3.3.2")
    application
}

group = "qwikk.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.22")
    implementation("org.json:json:20231013")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

application {
    mainClass.set("MainKt")
}

jib {
    from {
        image = "openjdk:latest"
    }
    to {
        image = "qwikk/f1bot"
    }
}