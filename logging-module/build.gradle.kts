plugins {
    id("java-library")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation("org.springframework.kafka:spring-kafka") // Kafka starter
    implementation("org.springframework.boot:spring-boot-starter-webflux") // WebFlux starter
    implementation("org.springframework.boot:spring-boot-starter-validation") // Validation starter

    implementation(project(":common-module"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "Logging Module"
        attributes["Implementation-Version"] = version
        attributes["Implementation-Vendor"] = "Your Company" // 발행자 정보 추가
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
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("developerId")
                        name.set("Developer Name")
                        email.set("developer@example.com")
                    }
                }
                scm {
                    url.set("http://www.example.com/scm")
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("http://localhost:8081/repository/maven-releases/")
            isAllowInsecureProtocol = true
            credentials {
                username = findProperty("nexusUsername") as String? ?: System.getenv("NEXUS_USERNAME") ?: "admin"
                password = findProperty("nexusPassword") as String? ?: System.getenv("NEXUS_PASSWORD") ?: "admin"
            }
        }
    }
}
