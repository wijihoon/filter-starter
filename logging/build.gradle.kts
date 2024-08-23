import org.springframework.boot.gradle.tasks.bundling.BootJar

// Access shared properties
val artifactName: String by rootProject.extra
val projectOwner: String by rootProject.extra

plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

// Project metadata and dependencies
dependencies {
    implementation("org.springframework.kafka:spring-kafka") // Kafka starter
    implementation("org.springframework.boot:spring-boot-starter-webflux") // WebFlux starter

    implementation(project(":common"))
}

// Jar settings
tasks.withType<Jar> {
    archiveFileName.set("${artifactName.replace(" ", "-").lowercase()}-${version}.jar")
}

// BootJar settings (if needed)
tasks.withType<BootJar> {
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
