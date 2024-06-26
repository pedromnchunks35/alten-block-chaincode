/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.8/userguide/building_java_projects.html in the Gradle documentation.
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    jcenter() // JCenter repository
    maven { url = uri("https://jitpack.io") } // JitPack repository
}

dependencies {
    // This dependency is used by the application.
    implementation(libs.guava)
    implementation("org.hyperledger.fabric-chaincode-java:fabric-chaincode-shim:2.5.+")
    implementation("org.json:json:+")
    implementation("com.owlike:genson:1.5")
    implementation("org.junit.jupiter:junit-jupiter:5.10.2")
    implementation("org.assertj:assertj-core:3.25.3")
    implementation("org.mockito:mockito-core:5.12.0")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit4 test framework
            useJUnit("4.13.2")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    // Define the main class for the application.
    mainClass = "org.example.App"
}