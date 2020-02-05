import java.net.URI
val config4kVersion = "0.4.1"

plugins {
    kotlin("jvm") version "1.3.61"
}

group = "com.j0rsa.tutorial"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven { url = URI("http://dl.bintray.com/kotlin/exposed") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.github.config4k:config4k:$config4kVersion")
    implementation("com.h2database:h2:1.4.198")
    implementation("org.jetbrains.exposed:exposed:0.17.7")

    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.20")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}