plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    implementation(project(":common-module"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "SQL Injection Module"
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
                name.set("SQL Injection Module")
                description.set("Module for filtering SQL injection attacks.")
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
