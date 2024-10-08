plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    implementation(project(":common-module"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "XSS Module"
        attributes["Implementation-Version"] = version
        attributes["Implementation-Vendor"] = "Shinhancard"
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
                name.set("XSS Module")
                description.set("XSS module for filtering XSS attacks.")
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
