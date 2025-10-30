plugins {
    java
    kotlin("jvm") version "2.2.20"
    application
}

group = "kim.hhhhhy.mock.care"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("kim.hhhhhy.mock.care.MainKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "kim.hhhhhy.mock.care.MainKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
kotlin {
    jvmToolchain(8)
}