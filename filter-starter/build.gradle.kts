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
                url.set("https://gitlab.shinhancard.com/genensis/genesis-autoconfigure")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("opensource.org/licenses/mit/")
                    }
                }
                developers {
                    developer {
                        id.set("@203933")
                        name.set("Shinhancard Genesis")
                        email.set("wjh@shinhan.com")
                    }
                }
                scm {
                    url.set("https://gitlab.shinhancard.com/genensis/genesis-autoconfigure/scm")
                    connection.set("scm:git:https://gitlab.shinhancard.com/genensis/genesis-autoconfigure.git")
                    developerConnection.set("scm:git:https://gitlab.shinhancard.com/genensis/genesis-autoconfigure.git")
                    tag.set("v1.0.0")
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
