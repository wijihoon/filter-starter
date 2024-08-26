# Filter Starter Auto Configuration

`filter-starter` 모듈은 Spring Boot 애플리케이션에서 다양한 보안 및 로깅 필터를 자동으로 구성하는 기능을 제공합니다. 이 모듈은 다양한 필터를 자동으로 등록하여 애플리케이션의 보안을 강화하고
로깅을 지원합니다.

## 기능

- **자동 구성**: 다양한 보안 및 로깅 필터를 자동으로 등록하여 설정 및 초기화를 간소화합니다.
- **동적 활성화**: 필터들은 조건에 따라 동적으로 활성화되며, 외부 설정에 따라 동작을 조절할 수 있습니다.

## 구성 요소

이 모듈은 다음과 같은 보안 및 로깅 필터를 자동으로 구성합니다:

- **CORS 필터**: [CorsAutoConfiguration](#cors-autoconfiguration) 클래스에 의해 제공됩니다.
- **로그 필터**: [LogAutoConfiguration](#log-autoconfiguration) 클래스에 의해 제공됩니다.
- **CSRF 필터**: [CsrfAutoConfiguration](#csrf-autoconfiguration) 클래스에 의해 제공됩니다.
- **XSS 필터**: [XSSAutoConfiguration](#xss-autoconfiguration) 클래스에 의해 제공됩니다.
- **SQL 인젝션 필터**: [SQLInjectionAutoConfiguration](#sqlinjection-autoconfiguration) 클래스에 의해 제공됩니다.

## 클래스 설명

### FilterStarterAutoConfiguration

`FilterStarterAutoConfiguration` 클래스는 다양한 보안 및 로깅 필터를 자동으로 구성하는 설정 클래스입니다. 이 클래스는 Spring Boot의 자동 구성 메커니즘을 통해 필요한 필터들을
등록합니다. 필터들은 조건에 따라 동적으로 활성화되며, 외부 설정에 따라 동작을 조절할 수 있습니다.

- **설정 설명**: 이 클래스는 여러 `AutoConfiguration` 클래스를 지정하여 필터들을 자동으로 설정합니다.
- **등록된 필터**:
    - `CorsAutoConfiguration`
    - `LogAutoConfiguration`
    - `CsrfAutoConfiguration`
    - `XSSAutoConfiguration`
    - `SQLInjectionAutoConfiguration`

### 설정 방법

필터들을 활성화하거나 비활성화하려면, 각 필터의 구성 클래스를 통해 설정을 조정할 수 있습니다. 다음은 각 필터의 설정 방법입니다:

#### CorsAutoConfiguration

- **설명**: CORS (Cross-Origin Resource Sharing) 설정을 관리합니다.
- **설정 파일**: `application.properties` 또는 `application.yml`

#### LogAutoConfiguration

- **설명**: 로그 설정을 관리합니다. 로그를 Kafka 또는 Loki로 전송할 수 있습니다.
- **설정 파일**: `application.properties` 또는 `application.yml`

#### CsrfAutoConfiguration

- **설명**: CSRF (Cross-Site Request Forgery) 보호를 위한 설정을 관리합니다.
- **설정 파일**: `application.properties` 또는 `application.yml`

#### XSSAutoConfiguration

- **설명**: XSS (Cross-Site Scripting) 공격을 방지하는 필터를 설정합니다.
- **설정 파일**: `application.properties` 또는 `application.yml`

#### SQLInjectionAutoConfiguration

- **설명**: SQL 인젝션 공격을 방지하는 필터를 설정합니다.
- **설정 파일**: `application.properties` 또는 `application.yml`

## 사용 방법

이 모듈을 사용하려면, `build.gradle` 또는 `pom.xml` 파일에 `filter-starter` 의존성을 추가하고, 필요한 설정을 외부 구성 파일에서 조정하면 됩니다.

### Gradle

```groovy
dependencies {
    implementation 'com.example:filter-starter:1.0.0'
}
```

```maven
<dependency>
    <groupId>com.example</groupId>
    <artifactId>filter-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 예제

다음은 application.properties 또는 application.yml 파일에서 필터를 활성화하거나 설정하는 예제입니다:

```properties
# CORS 설정 예제
cors.allowedOrigins=http://example.com
cors.allowedMethods=GET,POST,PUT,DELETE

# 로그 설정 예제
log.logDestination=kafka
log.sensitiveFields=password,cardNumber

# CSRF 설정 예제
csrf.enabled=true

# XSS 설정 예제
xss.enabled=true

# SQL 인젝션 설정 예제
sqlInjection.enabled=true
```

```yaml
cors:
  allowedOrigins:
    - http://example.com
  allowedMethods:
    - GET
    - POST
    - PUT
    - DELETE

log:
  logDestination: kafka
  sensitiveFields:
    - password
    - cardNumber

csrf:
  enabled: true

xss:
  enabled: true

sqlInjection:
  enabled: true
```
