plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    implementation(project(":common-module"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Implementation-Title"] = "Csrf Module"
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
                name.set("Csrf Module")
                description.set("Csrf module for handling CSRF attacks.")
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
