/*
 * This file was generated by the Gradle 'init' task.
 */

val kotlinVersion = "1.7.10"

plugins {
    application
    java
    `maven-publish`
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10"
    id("com.github.ben-manes.versions") version "0.39.0"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

group = "dev.chieppa"
version = "2.0-SNAPSHOT"
description = "unofficial_ao3_wrapper"
java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("org.slf4j:slf4j-api:2.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.0")
    testImplementation("org.assertj:assertj-core:3.23.1")
//    testImplementation("org.jetbrains.kotlin:kotlin-test")
//    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
//    testImplementation("io.kotest:kotest-runner-junit5-jvm")
//    testImplementation("io.kotest:kotest-assertions-core-jvm:4.6.3")
//    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}

val KtorVersion: String = "2.1.1"

dependencies {
    implementation("io.ktor:ktor-client-core:$KtorVersion")
    implementation("io.ktor:ktor-client-cio:$KtorVersion")
    implementation("io.ktor:ktor-client-logging:$KtorVersion")

    implementation("io.ktor:ktor-client-content-negotiation:$KtorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$KtorVersion")
}

publishing {
    publications.create<MavenPublication>("maven") {
        this.groupId = "dev.chieppa"
        this.artifactId = "ao3_wrapper"
        this.version = "1.0"

        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

