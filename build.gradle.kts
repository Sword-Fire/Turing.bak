import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.geekmc.turing"
version = "0.1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://repo.spongepowered.org/maven") }
    maven { url = uri("https://www.jitpack.io") }
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

    implementation("org.apache.logging.log4j:log4j-core:2.19.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

    implementation("com.github.Project-Cepi:KStom:f02e4c21d4")

    implementation("net.kyori:adventure-text-minimessage:4.11.0")

    implementation("org.yaml:snakeyaml:1.32")

    implementation("com.github.Minestom:Minestom:-SNAPSHOT") {
        exclude("org.tinylog")
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("")
        mergeServiceFiles()
        dependencies {
            exclude("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
            exclude("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            exclude("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.0")
            exclude("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
        }
        manifest {
            attributes("Main-Class" to "net.geekmc.turing.Turing")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}