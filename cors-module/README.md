# CORS Module

`cors-module`은 Spring Boot 애플리케이션에서 CORS (Cross-Origin Resource Sharing) 설정을 관리하는 모듈입니다. 이 모듈은 외부 도메인에서의 요청을 제어하고, CORS
정책을 적용하여 보안을 강화합니다.

## 기능

- **CORS 설정 관리**: 외부 도메인에서의 요청을 허용하거나 차단하는 설정을 제공합니다.
- **커스텀 CORS 필터**: CORS 요청의 출처, 메서드, 헤더를 검증하여 CORS 정책을 준수하지 않는 요청에 대해 에러 응답을 처리합니다.

## 의존성

이 모듈을 사용하려면 `build.gradle` 또는 `pom.xml`에 다음과 같은 의존성을 추가해야 합니다.

### Gradle

```groovy
dependencies {
    implementation 'ent.genesisframework:cors-module:1.0.0'
}
```

```Maven
<dependency>
    <groupId>ent.genesisframework</groupId>
    <artifactId>cors-module</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 클래스 설명

### `CorsAutoConfiguration`

`CorsAutoConfiguration` 클래스는 Spring Boot의 자동 구성 기능을 활용하여 CORS 설정을 애플리케이션에 자동으로 적용합니다. 이 클래스는 `CorsProperties`에서 CORS 관련
설정 값을 읽어와 `CorsConfigurationSource`와 `CorsFilter`를 등록합니다. 이를 통해 애플리케이션이 시작될 때 CORS 관련 필터가 자동으로 설정되며, 사용자 정의 CORS 정책이
적용됩니다.

- **주요 역할**:
    - `CorsProperties`에서 읽어온 설정을 기반으로 `CorsConfiguration` 객체를 생성합니다.
    - `CorsConfigurationSource`를 빈으로 등록하여 CORS 설정을 제공하는 소스를 정의합니다.
    - `CorsFilter`를 빈으로 등록하여 HTTP 요청에 대한 CORS 검증을 수행합니다.

### `CorsFilter`

`CorsFilter` 클래스는 HTTP 요청이 CORS 정책에 부합하는지 검증하는 필터입니다. 이 클래스는 `OncePerRequestFilter`를 확장하여 요청당 한 번만 실행되며, CORS 요청의 출처,
메서드, 헤더를 검증하여 정책에 맞지 않는 요청에 대해서는 적절한 에러 응답을 반환합니다.

- **주요 역할**:
    - 요청의 CORS 관련 헤더를 로깅하여 디버깅과 모니터링을 지원합니다.
    - `CorsConfigurationSource`에서 CORS 설정을 가져와 검증을 수행합니다.
    - 출처(origin), HTTP 메서드, 헤더가 CORS 정책에 맞지 않는 경우 에러 응답을 반환합니다.
    - 모든 출처, 메서드, 헤더를 허용하는 설정이 있는 경우, 필터 체인을 계속 진행합니다.

### `CorsProperties`

`CorsProperties` 클래스는 CORS 설정을 외부 구성 파일에서 관리하는 프로퍼티 클래스입니다. 이 클래스는 Spring Boot의 `@ConfigurationProperties`를
사용하여 `application.yml` 파일에서 CORS 관련 설정을 읽어옵니다. 설정 값은 CORS 정책을 정의하는 데 사용됩니다.

- **주요 역할**:
    - 허용할 도메인 목록(`allowedOrigins`), HTTP 메서드 목록(`allowedMethods`), HTTP 헤더 목록(`allowedHeaders`), 자격 증명 포함
      여부(`allowCredentials`)를 설정합니다.
    - 기본값으로 모든 도메인, 메서드, 헤더를 허용하며, 자격 증명 포함 여부는 `true`로 설정됩니다.
    - `application.yml` 파일에서 이 설정들을 통해 CORS 정책을 정의하고 조정할 수 있습니다.

## 설정 방법

### 1. `application.yml` 설정

`application.yml` 파일에 CORS 설정을 추가합니다. 아래는 설정 예시입니다:

```yaml
filter:
  cors:
    enabled: false
    allowedOrigins:
      - "http://example.com"
      - "http://another-domain.com"
    allowedMethods:
      - "GET"
      - "POST"
      - "PUT"
      - "DELETE"
    allowedHeaders:
      - "Authorization"
      - "Content-Type"
    allowCredentials: true
```

```properties
# 필터 활성화
filter.cors.enabled=false
# 허용할 출처 목록
filter.cors.allowedOrigins=http://example.com,http://another-domain.com
# 허용할 HTTP 메서드 목록
filter.cors.allowedMethods=GET,POST,PUT,DELETE
# 허용할 HTTP 헤더 목록
filter.cors.allowedHeaders=Authorization,Content-Type
# 자격 증명(쿠키 등) 포함 허용 여부
filter.cors.allowCredentials=true
```

이 설정은 다음을 정의합니다:

- **allowedOrigins**: 허용할 도메인 목록
- **allowedMethods**: 허용할 HTTP 메서드 목록
- **allowedHeaders**: 허용할 HTTP 헤더 목록
- **allowCredentials**: 자격 증명(쿠키 등)을 포함한 요청 허용 여부

## 요청 / 응답 예시

### 1. 출처 (Origin) 정책 위반

#### 요청 예시

- **출처 (Origin)**: `https://unauthorized-origin.com`
- **HTTP 메서드**: `GET`
- **헤더**: `Content-Type: application/json`

#### 응답 예시

- **상태 코드**: `403 Forbidden`
- **응답 바디**:

    ```json
    {
        "success": false,
        "message": "CORS 출처 정책 위반",
        "data": null,
        "status": 403
    }
    ```

### 2. 메서드 (Method) 정책 위반

#### 요청 예시

- **출처 (Origin)**: `https://example.com`
- **HTTP 메서드**: `DELETE` (허용되지 않은 메서드)
- **헤더**: `Content-Type: application/json`

#### 응답 예시

- **상태 코드**: `403 Forbidden`
- **응답 바디**:

    ```json
    {
        "success": false,
        "message": "CORS 메서드 정책 위반",
        "data": null,
        "status": 403
    }
    ```

### 3. 헤더 (Headers) 정책 위반

#### 요청 예시

- **출처 (Origin)**: `https://example.com`
- **HTTP 메서드**: `GET`
- **헤더**: `Content-Type: application/xml` (허용되지 않는 헤더)

#### 응답 예시

- **상태 코드**: `403 Forbidden`
- **응답 바디**:

    ```json
    {
        "success": false,
        "message": "CORS 헤더 정책 위반",
        "data": null,
        "status": 403
    }
    ```