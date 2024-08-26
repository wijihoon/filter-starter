plugins {
    id("java-library")
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    implementation(project(":common-module"))
}

tasks.jar {
    manifest {
        attributes["Implementation-Title"] = "XSS Module"
        attributes["Implementation-Version"] = version
        attributes["Implementation-Vendor"] = "Your Company"
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
