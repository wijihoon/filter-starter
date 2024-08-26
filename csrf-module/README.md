# CSRF Module

`csrf-module`은 Spring Boot 애플리케이션에서 CSRF(Cross-Site Request Forgery) 공격을 방지하기 위한 필터 및 설정을 제공하는 모듈입니다. 이 모듈은 보안 관련 HTTP
헤더를 응답에 추가하여 CSRF 공격으로부터 보호합니다.

## 기능

- **보안 헤더 추가**: HTTP 응답에 보안 헤더를 추가하여 CSRF 및 기타 보안 위협을 방지합니다.
- **설정 관리**: 보안 헤더의 설정을 외부 구성 파일에서 관리할 수 있습니다.

## 의존성

이 모듈을 사용하려면 `build.gradle` 또는 `pom.xml`에 다음과 같은 의존성을 추가해야 합니다.

### Gradle

```groovy
dependencies {
    implementation 'com.example:csrf-module:1.0.0'
}
```

```Maven
<dependency>
    <groupId>com.example</groupId>
    <artifactId>csrf-module</artifactId>
    <version>1.0.0</version>
</dependency> 
```

## 설정

`application.properties` 또는 `application.yml` 파일에서 다음과 같이 설정할 수 있습니다:

```properties
# CSRF 필터 활성화
filter.csrf.enabled=true
# X-Frame-Options 헤더의 값
csrf.headers.xFrameOptions=DENY
# X-XSS-Protection 헤더의 값
csrf.headers.xXssProtection=1; mode=block
# X-Content-Type-Options 헤더의 값
csrf.headers.xContentTypeOptions=nosniff

```

```yaml
filter:
  csrf:
    enabled: true

csrf:
  headers:
    xFrameOptions: DENY
    xXssProtection: 1; mode=block
    xContentTypeOptions: nosniff
```

## 응답 예시

```http
HTTP/1.1 200 OK
Content-Type: application/json
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block
X-Content-Type-Options: nosniff
```