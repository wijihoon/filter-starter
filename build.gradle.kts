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
}

subprojects {
    apply(plugin = "java") // 각 서브프로젝트에 java 플러그인 적용
    apply(plugin = "org.springframework.boot") // 각 서브프로젝트에 java 플러그인 적용
    apply(plugin = "io.spring.dependency-management") // 각 서브프로젝트에 java 플러그인 적용

    group = "ent.genesisframework"
    version = "1.0.7"

    repositories {
        mavenCentral()
        maven("https://repo.spring.io/milestone")
        maven("https://repo.spring.io/snapshot")
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")

        // Lombok 라이브러리
        implementation("org.projectlombok:lombok") // Lombok 라이브러리
        annotationProcessor("org.projectlombok:lombok") // Lombok annotation processor
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
