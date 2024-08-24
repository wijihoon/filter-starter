plugins {
    id("java-library")
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("maven-publish")
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
