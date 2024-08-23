// 프로젝트 메타데이터 설정
val artifactName = "Private Label Credit Card API"
val projectOwner = "Shinhancard Platform Development"
val repositoryURL = "https://gitlab.shinhancard.com/pla/oauth-backend"
val licenseName = "MIT License"
val licenseURL = "https://opensource.org/license/mit/"

// 플러그인 버전 설정
plugins {
    id("java") // Java 플러그인
}

// Jar 및 BootJar 설정
tasks.withType<Jar> {
    archiveFileName.set("${artifactName.replace(" ", "-")}-${version}.jar") // 아카이브 파일 이름
}

// 의존성 설정
dependencies {
    implementation(project(":common"))
}
