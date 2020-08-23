plugins {
    java
    eclipse
    id("com.github.hierynomus.license")
}

repositories {
    jcenter()
    maven("https://repo-new.spongepowered.org/repository/maven-public")
   // maven("https://repo.spongepowered.org/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    // maven("https://jitpack.io")
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":nucleus-core"))
    implementation(project(":nucleus-modules"))

    val dep = "org.spongepowered:spongeapi:" + rootProject.properties["spongeApiVersion"]
    annotationProcessor(dep)
    implementation(dep)

    testCompile("org.mockito:mockito-all:1.10.19")
    testCompile("org.powermock:powermock-module-junit4:1.6.4")
    testCompile("org.powermock:powermock-api-mockito:1.6.4")
    testCompile("org.hamcrest:hamcrest-junit:2.0.0.0")
    testCompile("junit", "junit", "4.12")
}
