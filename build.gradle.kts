plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version("8.1.1")
    application
}

group = "qwikk.kotlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.12")
    implementation("org.json:json:20230618")

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