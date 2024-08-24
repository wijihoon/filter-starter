plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    implementation("org.springframework.kafka:spring-kafka") // Kafka starter
    implementation("org.springframework.boot:spring-boot-starter-webflux") // WebFlux starter
    implementation("org.springframework.boot:spring-boot-starter-validation") // Validation을 위한 의존성

    implementation(project(":common-module"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "Logging Module"
        attributes["Implementation-Version"] = version
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Logging Module")
                description.set("Logging module for handling logs.")
                url.set("http://www.example.com")
            }
        }
    }
    repositories {
        maven {
            url = uri("http://localhost:8081/repository/maven-releases/")
            isAllowInsecureProtocol = true
            credentials {
                username = project.findProperty("nexusUsername") as String? ?: "admin"
                password = project.findProperty("nexusPassword") as String? ?: "admin"
            }
        }
    }
}
