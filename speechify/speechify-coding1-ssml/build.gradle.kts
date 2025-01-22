plugins {
    kotlin("jvm") version "1.8.21"
}

group = "com.speechify.platform"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
}

tasks.test {
    useJUnitPlatform()
    reports {
        junitXml.required.set(true)
        junitXml.outputLocation.set(file("${buildDir}/test-results/junitXml"))
        html.required.set(true)
        html.outputLocation.set(file("${buildDir}/test-results/junitHtml"))
    }
}

kotlin {
    jvmToolchain(11)
}