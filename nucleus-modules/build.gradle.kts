plugins {
    java
    eclipse
}

group = "io.github.nucleuspowered"

repositories {
    jcenter()
    maven("https://repo-new.spongepowered.org/repository/maven-public")
    maven("https://repo.drnaylor.co.uk/artifactory/list/minecraft")
    maven("https://repo.drnaylor.co.uk/artifactory/list/quickstart")
    // maven("https://jitpack.io")
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
        resources {
            srcDir("src/main/resources")
            exclude("assets/nucleus/suggestions/**")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":nucleus-api"))
    implementation(project(":nucleus-core"))

    val dep = "org.spongepowered:spongeapi:${rootProject.properties["spongeApiVersion"]}"
    annotationProcessor(dep)
    implementation(dep) {
        exclude("org.spongepowered", "configurate-core")
        exclude("org.spongepowered", "configurate-gson")
        exclude("org.spongepowered", "configurate-hocon")
        exclude("org.spongepowered", "configurate-yaml")
    }

    testCompile("org.mockito:mockito-all:1.10.19")
    testCompile("org.powermock:powermock-module-junit4:1.6.4")
    testCompile("org.powermock:powermock-api-mockito:1.6.4")
    testCompile("org.hamcrest:hamcrest-junit:2.0.0.0")
    testCompile("junit", "junit", "4.12")
}
