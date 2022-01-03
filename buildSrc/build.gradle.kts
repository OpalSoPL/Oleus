buildscript {
    val properties = java.util.Properties()
    val input = java.io.FileInputStream(file("../gradle.properties"))
    properties.load(input)
    input.close()

    val kotlinVersion: String? = properties.getProperty("kotlinVersion")
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("org.apache.httpcomponents:httpmime:4.5.3")
        classpath("com.google.code.gson:gson:2.8.6")
    }
}
plugins {
    java
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

apply(plugin = "org.jetbrains.kotlin.jvm")

repositories {
    jcenter()
}

dependencies {
    gradleApi()
    implementation(kotlin("stdlib-jdk8", "1.3.61"))
    implementation("org.apache.httpcomponents:httpmime:4.5.3")
    implementation("com.google.code.gson:gson:2.8.6")
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}