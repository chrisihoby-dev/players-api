import org.gradle.api.JavaVersion.VERSION_21

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val arrowVersion: String by project
val koinKtor: String by project
val kotestVersion: String by project
val kotlinCoroutineVersion: String by project

plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
    id("io.kotest") version "0.4.11"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.tournament"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()

}

dependencies {
    // Ktor Server dependencies
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-cors")
    // Arrow dependencies
    implementation("io.arrow-kt:arrow-core:$arrowVersion")

    // Koin for Ktor
    implementation("io.insert-koin:koin-ktor:$koinKtor")
    implementation("io.insert-koin:koin-logger-slf4j:$koinKtor")

    // Ktor Client dependencies
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // DynamoDB
    implementation("aws.sdk.kotlin:dynamodb:1.1.23")

    // api documentation
    implementation("io.github.smiley4:ktor-swagger-ui:2.9.0")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.19")

    // Logback
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // Test dependencies
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.3.3")
    implementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-koin:1.3.0")
    testImplementation("io.mockk:mockk:1.13.10")
    testImplementation("org.testcontainers:testcontainers:1.19.7")


}

ktor {
    docker {
        jreVersion.set(VERSION_21)
        localImageName.set(project.name)
        imageTag.set(version.toString())

    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
tasks.jar {
    enabled = false
}

tasks.distTar {
    enabled = false
}
tasks.distZip {
    enabled = false
}

tasks.shadowDistTar {
    enabled = false
}
tasks.shadowDistZip {
    enabled = false
}
tasks.buildImage {
    enabled = true
}

tasks.shadowJar {
    isZip64 = true
    archiveFileName.set("${project.name}.jar")
}
