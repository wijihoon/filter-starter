plugins {
    `java-library`
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
        maven("https://repo.spring.io/snapshot")
    }

    // 버전 관리를 위한 공통 변수
    ext {
        set("lombokVersion", "1.18.24")
        set("javaVersion", "21")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    group = "ent.genesisframework"
    version = "1.0.0"

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")

        // Lombok 라이브러리
        compileOnly("org.projectlombok:lombok:${project.ext["lombokVersion"]}")
        annotationProcessor("org.projectlombok:lombok:${project.ext["lombokVersion"]}")
    }
}
