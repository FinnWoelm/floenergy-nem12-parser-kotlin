plugins {
    kotlin("jvm") version "1.9.23"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.slf4j:slf4j-nop:2.0.12")
    implementation("org.jooq:jooq:3.19.6")
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
}

tasks.test {
    useJUnitPlatform()
}
