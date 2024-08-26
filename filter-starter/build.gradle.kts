plugins {
    id("java-library")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(project(":common-module"))
    implementation(project(":logging-module"))
    implementation(project(":cors-module"))
    implementation(project(":csrf-module"))
    implementation(project(":xss-module"))
    implementation(project(":sql-injection-module"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "Filter Starter"
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
                name.set("Filter Starter")
                description.set("Starter module that integrates all filters and services.")
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
