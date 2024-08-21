// 프로젝트 메타데이터 설정
val artifactName = "Private Label Credit Card API"
val projectOwner = "Shinhancard Platform Development"
val repositoryURL = "https://gitlab.shinhancard.com/pla/oauth-backend"
val licenseName = "MIT License"
val licenseURL = "https://opensource.org/license/mit/"

// 플러그인 버전 설정
plugins {
    id("java") // Java 플러그인
    id("org.springframework.boot") version "3.2.2" // Spring Boot 플러그인
    id("io.spring.dependency-management") version "1.1.4" // 의존성 관리 플러그인
    id("com.github.ben-manes.versions") version "0.46.0" // 의존성 업데이트 플러그인
}

// Java 버전 설정
java {
    sourceCompatibility = JavaVersion.VERSION_21 // 소스 호환성
    targetCompatibility = JavaVersion.VERSION_21 // 타겟 호환성
}

// 의존성 버전 관리
val versions = mapOf(
        "logback" to "1.4.12", // Logback 버전
        "slf4j" to "2.0.7", // SLF4J 버전
        "jjwt" to "0.9.1", // JWT 버전
        "springdoc" to "2.3.0", // Springdoc OpenAPI 버전
        "junit" to "5.10.0", // JUnit 버전
        "mockito" to "5.12.0", // Mockito 버전
        "assertj" to "3.26.0", // AssertJ 버전
        "commons-pool" to "2.12.0", // Commons Pool 버전
        "lettuce-core" to "6.3.2" // Lettuce Core 버전
)

// 프로젝트 메타데이터 설정
group = "com.shinhancard" // 그룹 ID
version = "0.0.1-SNAPSHOT" // 버전

// Jar 및 BootJar 설정
tasks.bootJar {
    manifest {
        attributes(
                "Implementation-Title" to artifactName, // 구현 제목
                "Automatic-Module-Name" to artifactName, // 자동 모듈 이름
                "Implementation-Version" to version, // 구현 버전
                "Created-By" to projectOwner // 프로젝트 소유자
        )
    }
    archiveFileName.set("${artifactName.replace(" ", "-")}-${version}.jar") // 아카이브 파일 이름
    isEnabled = true
}

tasks.jar {
    isEnabled = false // 기본 Jar 작업 비활성화
}

// 리포지토리 설정
repositories {
    mavenCentral() // 기본 Maven Central Repository
    maven { url = uri("https://repo.spring.io/milestone") } // Spring Milestone Repository
    maven { url = uri("https://repo.spring.io/snapshot") } // Spring Snapshot Repository
}

// 의존성 설정
dependencies {
    // Spring Boot 관련 의존성
    implementation("org.springframework.boot:spring-boot-starter-web") // 웹 스타터
    implementation("org.springframework.boot:spring-boot-starter-data-redis") // Redis 데이터 스타터
    implementation("org.springframework.boot:spring-boot-starter-security") // 보안 스타터
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client") // OAuth2 클라이언트 스타터
    implementation("org.springframework.boot:spring-boot-starter-validation") // 검증 스타터
    implementation("org.springframework.kafka:spring-kafka") // Kafka 스타터

    // JWT 관련 라이브러리
    implementation("io.jsonwebtoken:jjwt:${versions["jjwt"]}") // JJWT
    // JAXB API
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    // JAXB Runtime
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.1")

    // 로깅 관련 라이브러리
    implementation("ch.qos.logback:logback-classic:${versions["logback"]}") // Logback Classic
    implementation("org.slf4j:slf4j-api:${versions["slf4j"]}") // SLF4J API

    // Lombok 라이브러리
    compileOnly("org.projectlombok:lombok") // Lombok
    annotationProcessor("org.projectlombok:lombok") // Lombok Annotation Processor
    testCompileOnly("org.projectlombok:lombok") // Lombok for Test
    testAnnotationProcessor("org.projectlombok:lombok") // Lombok Annotation Processor for Test

    // Springdoc OpenAPI 관련 라이브러리
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${versions["springdoc"]}") // Springdoc OpenAPI Starter Web MVC UI

    implementation("org.apache.commons:commons-pool2:${versions["commons-pool"]}") // Commons Pool2

    // 테스트 관련 의존성
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine") // JUnit Vintage 제외
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api:${versions["junit"]}") // JUnit Jupiter API
    testImplementation("org.mockito:mockito-core:${versions["mockito"]}") // Mockito Core
    testImplementation("org.mockito:mockito-junit-jupiter:${versions["mockito"]}") // Mockito JUnit Jupiter
    testImplementation("org.assertj:assertj-core:${versions["assertj"]}") // AssertJ Core
    testImplementation("org.springframework.security:spring-security-test") // Spring Security Test
    testImplementation("io.lettuce:lettuce-core:${versions["lettuce-core"]}") // Lettuce Core
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${versions["junit"]}") // JUnit Jupiter Engine
}

// 테스트 및 컴파일 설정
tasks.withType<Test> {
    useJUnitPlatform() // JUnit 플랫폼 사용
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // UTF-8 인코딩 설정
}

// Gradle 의존성 설정
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get()) // Annotation Processor와 연계
    }
}

// Spring Boot 빌드 정보 설정
springBoot {
    buildInfo() // 빌드 정보 생성
}