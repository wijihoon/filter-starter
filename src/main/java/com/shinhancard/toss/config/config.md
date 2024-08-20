# CorsConfig

`CorsConfig` 클래스는 Spring 애플리케이션에서 CORS(Cross-Origin Resource Sharing) 설정을 적용합니다. 외부 도메인에서의 API 요청을 제어하는 역할을 합니다.

## 클래스 설명

- **목적**: 외부 도메인에서의 요청을 허용하거나 차단하는 CORS 필터를 설정합니다.
- **설정**: `CorsProperties` 클래스로부터 CORS 설정을 주입받아 필터를 구성합니다.

## 주요 메소드

### `corsFilter`

- **설명**: CORS 설정을 적용한 `CorsFilter`를 반환합니다.
- **반환값**: `CorsFilter`

## CORS 설정 항목

- **자격 증명 포함 여부**: `setAllowCredentials` 메소드로 자격 증명(쿠키, 인증 헤더 등) 포함 여부 설정
    - **설정값**: `corsProperties.isAllowCredentials()`

- **허용된 도메인**: `setAllowedOrigins` 메소드로 허용할 도메인 설정 (빈 경우 모든 도메인 허용)
    - **설정값**: `corsProperties.getAllowedOrigins()`

- **허용된 HTTP 메서드**: `setAllowedMethods` 메소드로 허용할 HTTP 메서드 설정
    - **설정값**: `corsProperties.getAllowedMethods()`

- **허용된 헤더**: `setAllowedHeaders` 메소드로 허용할 HTTP 헤더 설정
    - **설정값**: `corsProperties.getAllowedHeaders()`

## 사용 방법

- `@Configuration` 어노테이션을 사용하여 설정 클래스로 등록합니다.
- `@Bean` 어노테이션을 사용하여 `corsFilter` 메소드를 Bean으로 등록합니다.

---

# KafkaConfig 클래스 설명

`KafkaConfig` 클래스는 Kafka 프로듀서를 설정하기 위한 Spring Configuration 클래스입니다. 이 클래스는 `KafkaProperties` 클래스로부터 Kafka 설정을 주입받아 Kafka
프로듀서와 템플릿을 생성합니다.

## 주요 구성 요소

### `KafkaConfig` 클래스

- **역할**: Kafka 프로듀서와 관련된 설정을 정의합니다.
- **주입된 프로퍼티**: `KafkaProperties`

### 메서드

#### `producerFactory()`

- **설명**: Kafka 프로듀서 팩토리를 생성합니다.
- **반환 타입**: `ProducerFactory<String, String>`
- **설정**:
    - `ProducerConfig.BOOTSTRAP_SERVERS_CONFIG`: Kafka 서버 주소 설정
    - `ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG`: 키 직렬화기 설정
    - `ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG`: 값 직렬화기 설정

#### `kafkaTemplate()`

- **설명**: KafkaTemplate을 생성하여 Kafka 프로듀서와의 상호작용을 위한 템플릿을 제공합니다.
- **반환 타입**: `KafkaTemplate<String, String>`
- **동작**: `producerFactory()` 메서드를 사용하여 KafkaTemplate을 생성하고 반환합니다.

이 클래스는 Kafka와의 통신을 위한 기본 설정을 구성하며, `KafkaProperties`에서 제공하는 값을 바탕으로 Kafka 프로듀서와 템플릿을 설정합니다.

---

# OpenAPIConfig

`OpenAPIConfig` 클래스는 Spring 애플리케이션에서 OpenAPI 설정을 구성하고 Swagger UI에서 API 문서를 제공합니다.

## 클래스 설명

- **목적**: OpenAPI 스펙을 설정하고 Swagger UI에서 API 문서를 제공합니다.
- **설정**: `OpenAPIProperties`를 기반으로 OpenAPI 스펙을 생성합니다.

## 주요 메소드

### `customOpenAPI`

- **설명**: `OpenAPIProperties`에서 설정값을 기반으로 `OpenAPI` 인스턴스를 생성합니다.
- **반환값**: `OpenAPI`

## 설정 항목

- **Contact 정보**: API의 제공자와 관련된 세부 정보 설정
    - **설정값**: `name`, `url`, `email`

- **OpenAPI 인스턴스 정보**: API 문서의 제목, 버전, 설명 설정
    - **설정값**: `title`, `version`, `description`

- **서버 설정**: API 서버의 URL과 설명 설정
    - **설정값**: `url`, `description`

## 사용 방법

- `@Configuration` 어노테이션을 사용하여 설정 클래스로 등록합니다.
- `@Bean` 어노테이션을 사용하여 `customOpenAPI` 메소드를 Bean으로 등록합니다.

---

# RedisConfig

`RedisConfig` 클래스는 Spring 애플리케이션에서 Redis 설정을 구성하고, Redis와의 연결 및 데이터 상호작용을 관리합니다.

## 클래스 설명

- **목적**: Redis와의 연결을 설정하고 데이터 상호작용을 위한 `RedisTemplate`을 구성합니다.
- **설정**: `RedisProperties`로부터 설정을 주입받아 `LettuceConnectionFactory`와 `RedisTemplate`을 설정합니다.

## 주요 메소드

### `redisConnectionFactory`

- **설명**: Redis 서버와의 연결을 위한 `LettuceConnectionFactory`를 생성합니다.
- **반환값**: `LettuceConnectionFactory`

### `redisTemplate`

- **설명**: `RedisTemplate`을 설정하여 Redis와의 데이터 상호작용을 처리합니다.
- **파라미터**: `redisConnectionFactory`
- **반환값**: `RedisTemplate<String, Object>`

## 설정 항목

- **RedisConnectionFactory**: Redis 서버의 호스트, 포트, 비밀번호 설정
    - **설정값**: `host`, `port`, `password`

- **LettucePoolingClientConfiguration**: 커넥션 풀 및 타임아웃 설정
    - **설정값**: `commandTimeout`

- **RedisTemplate**: Redis와의 데이터 상호작용을 위한 템플릿 설정
    - **설정값**: `keySerializer`, `valueSerializer`

## 사용 방법

- `@Configuration` 어노테이션을 사용하여 설정 클래스로 등록합니다.
- `@Bean` 어노테이션을 사용하여 `redisConnectionFactory`와 `redisTemplate` 메소드를 Bean으로 등록합니다.

---

# SecurityConfig

`SecurityConfig` 클래스는 Spring Security를 사용하여 애플리케이션의 보안 설정을 구성합니다. 이 클래스는 OAuth2 인증, JWT 토큰 인증, CORS 필터, XSS 필터 등을 설정합니다.

## 클래스 설명

- **목적**: 애플리케이션의 보안을 강화하기 위한 다양한 보안 설정을 구성합니다.
- **설정 항목**:
    - CSRF 보호 비활성화
    - CORS 필터 및 XSS 필터 설정
    - JWT 인증 필터 설정
    - Content Security Policy (CSP) 설정
    - 세션 무상태 설정
    - 인증 실패 및 접근 권한 부족에 대한 예외 처리
    - Swagger UI와 API 문서 접근 허용

## 주요 메소드

### `securityFilterChain`

- **설명**: HTTP 보안 설정을 구성합니다. CSRF 보호 비활성화, CORS 및 XSS 필터 추가, 인증 및 권한 부여 설정, CSP 헤더 설정, 세션 무상태 설정 등 포함.
- **파라미터**: `http`
- **반환값**: `SecurityFilterChain`

### `oAuth2UserService`

- **설명**: 기본 OAuth2 사용자 정보를 제공하는 서비스입니다.
- **반환값**: `OAuth2UserService<OAuth2UserRequest, OAuth2User>`

## 사용 방법

- `@Configuration`과 `@EnableWebSecurity` 어노테이션을 사용하여 설정 클래스로 등록합니다.
- `@Bean` 어노테이션을 사용하여 `securityFilterChain`과 `oAuth2UserService` 메소드를 Bean으로 등록합니다.

