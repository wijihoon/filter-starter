import org.springframework.boot.gradle.tasks.bundling.BootJar

// Define project metadata
extra["artifactName"] = "Private Label Credit Card API"
extra["projectOwner"] = "Shinhancard Platform Development"

// Plugins and version settings
plugins {
    id("java")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

// Common project settings
allprojects {
    group = "com.shinhancard"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
        maven("https://repo.spring.io/snapshot")
    }
}

// Shared configuration for subprojects
subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    // Access shared properties
    val artifactName: String by rootProject.extra
    val projectOwner: String by rootProject.extra

    tasks.withType<Jar> {
        archiveFileName.set("${artifactName.replace(" ", "-").lowercase()}-${version}.jar")
    }

    tasks.withType<BootJar> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(
                    "Implementation-Title" to artifactName,
                    "Automatic-Module-Name" to artifactName,
                    "Implementation-Version" to version,
                    "Created-By" to projectOwner
            )
        }
        isEnabled = true
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
    }
}

// Main project dependencies
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation(project(":common"))
    implementation(project(":cors"))
    implementation(project(":log"))
    implementation(project(":xss"))
    implementation(project(":csrf"))
}

// Spring Boot build info
springBoot {
    mainClass.set("shinhancard.core.FilterCoreApplication") // 메인 클래스를 지정합니다.
    buildInfo()
}

tasks.withType<BootJar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}