# 종합 README

이 문서는 여러 보안 모듈을 포함하는 Spring Boot 애플리케이션을 위한 통합 README입니다. 각 모듈은 특정 보안 기능을 제공하며, 애플리케이션의 보안을 강화하는 데 도움을 줍니다.

## 모듈 목록

### 로깅 모듈 (Logging Module)

### SQL 인젝션 필터 모듈 (SQL Injection Filter Module)

### XSS 보호 모듈 (XSS Protection Module)

### 로깅 모듈 (Logging Module)

### CSRF 모듈 (CSRF Module)

### Shinhancard Common Wrapper 모듈

logging-module은 Spring Boot 애플리케이션에서 로그를 효율적으로 처리하고 전송할 수 있는 모듈입니다. 이 모듈은 Kafka와 Loki를 통해 로그를 전송할 수 있으며, 로그 필터와 설정 관리
기능도 제공합니다.

## 기능

- **로그 필터링**: HTTP 요청과 응답에서 로그를 필터링하여 전송합니다.
- **로그 전송**: Kafka와 Loki를 통해 로그를 전송할 수 있습니다.
- **설정 관리**: 로그 전송과 관련된 다양한 설정을 관리할 수 있습니다.

## 의존성

### Gradle

```groovy
dependencies {
    implementation 'ent.genesisframework:logging-module:1.0.0'
}
```

```Maven
<dependency>
    <groupId>ent.genesisframework</groupId>
    <artifactId>logging-module</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 로그 설정

application.properties 또는 application.yml 파일에서 로그 설정을 구성합니다.

application.properties

```properties
log.enabled=true
log.requestBody.truncate=true
log.responseBody.truncate=true
log.body.maxSize=2048
log.logDestination=loki
log.sensitiveFields=password,cardNumber,ssn
```

application.yml

```yaml
log:
    enabled: true
    requestBody:
      truncate: true
    responseBody:
      truncate: true
    body:
      maxSize: 2048
    logDestination: loki
    sensitiveFields:
        - password
        - cardNumber
        - ssn
```

## Kafka 설정

application.properties

```properties
kafka.bootstrapServers=localhost:9092
kafka.topicName=my-log-topic
kafka.acks=all
kafka.retries=3
kafka.batchSize=16384
kafka.lingerMs=5
kafka.compressionType=gzip
kafka.keySerializer=org.apache.kafka.common.serialization.StringSerializer
kafka.valueSerializer=org.apache.kafka.common.serialization.StringSerializer
```

application.yml

```yaml
kafka:
    bootstrapServers: localhost:9092
    topicName: my-log-topic
    acks: all
    retries: 3
    batchSize: 16384
    lingerMs: 5
    compressionType: gzip
    keySerializer: org.apache.kafka.common.serialization.StringSerializer
    valueSerializer: org.apache.kafka.common.serialization.StringSerializer
```

Loki 설정
application.properties

```properties
loki.url=http://localhost:3100
```

application.yml

```yaml
loki:
    url: http://localhost:3100
```

## SQL 인젝션 필터 모듈 (SQL Injection Filter Module)

이 모듈은 SQL 인젝션 공격을 탐지하고 방지하기 위한 필터를 제공합니다. Spring Boot 애플리케이션에 통합하여 사용하며, SQL 인젝션 공격으로부터 애플리케이션을 보호합니다.

### 주요 클래스

- **SQLInjectionAutoConfiguration**: SQL 인젝션 필터를 자동으로 구성하고 등록하는 설정 클래스입니다.
- **SQLInjectionFilter**: HTTP 요청의 파라미터, 본문 및 쿠키에서 SQL 인젝션 패턴을 검사합니다.
- **SQLInjectionProperties**: SQL 인젝션 패턴을 정의하는 설정 클래스입니다.

### 의존성 추가

Gradle

```groovy
코드 복사
dependencies {
    implementation 'shinhancard:sql-module:1.0.0'
}
```

### 설정 파일

application.properties

```properties
filter.sql.enabled=true
filter.sql.patterns[0]=.*([';]+|(--)).* # SQL 주석 및 SQL 주입
filter.sql.patterns[1]=.*union.*select.* # UNION SELECT
filter.sql.patterns[2]=.*select.*from.* # SELECT FROM
filter.sql.patterns[3]=.*insert.*into.* # INSERT INTO
filter.sql.patterns[4]=.*update.*set.* # UPDATE SET
filter.sql.patterns[5]=.*delete.*from.* # DELETE FROM
```

application.yml

```yaml
filter:
    sql:
    enabled: true
    patterns:
        - ".*([';]+|(--)).*" # SQL 주석 및 SQL 주입
        - ".*union.*select.*" # UNION SELECT
        - ".*select.*from.*" # SELECT FROM
        - ".*insert.*into.*" # INSERT INTO
        - ".*update.*set.*" # UPDATE SET
        - ".*delete.*from.*" # DELETE FROM
```

### 필터 동작

- **파라미터 검사**: 요청 파라미터의 값이 SQL 인젝션 패턴과 일치하면 ServletException을 발생시킵니다.
- **본문 검사**: 요청 본문이 SQL 인젝션 패턴과 일치하면 ServletException을 발생시킵니다.
- **쿠키 검사**: 쿠키 값이 SQL 인젝션 패턴과 일치하면 ServletException을 발생시킵니다.

### 요청 / 응답 예시

요청

```http
POST /api/user/login HTTP/1.1
Host: example.com
Content-Type: application/json

{
"username": "admin' OR '1'='1",
"password": "password"
}
```

응답

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
"success": false,
"message": "SQL Injection detected in request body",
"data": null,
"status": "BAD_REQUEST"
}
```

## XSS 보호 모듈 (XSS Protection Module)

이 XSS 보호 모듈은 Spring Boot Starter로, 애플리케이션에서 잠재적인 XSS(교차 사이트 스크립팅) 공격을 손쉽게 필터링하고 차단할 수 있도록 지원합니다. 이 모듈을 의존성으로 추가하면 필터와
관련된 설정이 자동으로 구성됩니다.

### 주요 기능

- **자동 XSS 필터링**: 요청의 파라미터, 쿠키, 본문에서 XSS 위협을 자동으로 감지합니다.
- **패턴 설정 가능**: XSS 공격을 탐지하는 정규식을 손쉽게 커스터마이징할 수 있습니다.
- **유연한 활성화/비활성화 옵션**: 설정을 통해 필터의 활성화 여부를 제어할 수 있습니다.

### 의존성 추가

Gradle

```groovy
dependencies {
    implementation 'ent.genesisframework:xss-module-starter:1.0.0'
}
```

### 설정 파일

#### 기본 설정

application.properties

```properties
filter.xss.enabled=true
filter.xss.patterns[0]=<script.*?>.*?</script>  # script 태그
filter.xss.patterns[1]=<.*?onerror\\s*=.*?>    # onerror 이벤트 핸들러
filter.xss.patterns[2]=<.*?onclick\\s*=.*?>     # onclick 이벤트 핸들러
# 필요에 따라 패턴 추가 또는 수정 가능
```

application.yml

```yaml
filter:
    xss:
    enabled: true # XSS 필터 활성화 여부 (기본값: true)
    patterns:
        - "<script.*?>.*?</script>"
        - "<.*?onerror\\s*=.*?>"
        - "<.*?onclick\\s*=.*?>"
# 필요에 따라 패턴 추가 또는 수정 가능
```

### 동작 방식

XSS 필터는 HTTP 요청의 다음 요소들을 검사합니다:

- **요청 파라미터**: 모든 요청 파라미터에서 XSS 공격을 탐지합니다.
- **쿠키**: 쿠키의 이름과 값을 검사하여 악성 스크립트를 감지합니다.
- **요청 본문**: 요청 본문이 XSS 패턴과 일치하면 요청을 차단하고 403 Forbidden 응답을 반환합니다.

### 고급 설정

필터의 동작을 세부적으로 조정하고 싶다면 아래와 같이 설정 파일을 통해 고급 설정을 적용할 수 있습니다.

application.properties

```properties
filter.xss.enabled=true
filter.xss.patterns[0]=<iframe.*?>.*?</iframe>  # iframe 태그
filter.xss.patterns[1]=<object.*?>.*?</object>  # object 태그
filter.xss.patterns[2]=<embed.*?>               # embed 태그
filter.xss.patterns[3]=<script.*?>.*?</script>  # script 태그
filter.xss.patterns[4]=<.*?javascript:.*?>      # javascript 프로토콜
filter.xss.patterns[5]=<.*?vbscript:.*?>        # vbscript 프로토콜
filter.xss.patterns[6]=<.*?data:.*?>            # data URIs
filter.xss.patterns[7]=<.*?expression\\(.*?>    # expressions
filter.xss.patterns[8]=<.*?onload\\s*=.*?>      # onload 이벤트 핸들러
# 추가 패턴 설정 가능
```

application.yml

```yaml
filter:
    xss:
        enabled: true
        patterns:
            - "<iframe.*?>.*?</iframe>"          # iframe 태그
            - "<object.*?>.*?</object>"          # object 태그
            - "<embed.*?>"                       # embed 태그
            - "<script.*?>.*?</script>"          # script 태그
            - "<.*?javascript:.*?>"              # javascript 프로토콜
            - "<.*?vbscript:.*?>"                # vbscript 프로토콜
            - "<.*?data:.*?>"                    # data URIs
            - "<.*?expression\\(.*?>"            # expressions
            - "<.*?onload\\s*=.*?>"              # onload 이벤트 핸들러
            # 추가 패턴 설정 가능
```

### 요청 / 응답 예시

요청

```http
POST /api/user/register HTTP/1.1
Host: example.com
Content-Type: application/json

{
"username": "attacker",
"email": "attacker@example.com",
"comment": "<script>alert('XSS Attack!');</script>"
}
```

응답

```http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
"success": false,
"message": "XSS detected in request body",
"data": null,
"status": "FORBIDDEN"
}
```

z# CSRF Module

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
    implementation 'ent.genesisframework:csrf-module:1.0.0'
}
```

```Maven
<dependency>
    <groupId>ent.genesisframework</groupId>
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

# Shinhancard Common Wrapper 모듈

`shinhancard.common.wrapper` 모듈은 Spring Boot 애플리케이션에서 HTTP 요청 및 응답 본문을 캐싱하고 래핑하는 유틸리티 클래스를 제공합니다. 이 유틸리티는 요청 및 응답 데이터를
여러 번 읽거나 수정할 수 있도록 도와줍니다.

## 기능

- **요청 본문 캐싱**: 요청 본문을 캐싱하여 여러 번 읽을 수 있도록 합니다.
- **응답 본문 캐싱**: 응답 본문을 캐싱하고 클라이언트에게 전송하기 전에 여러 번 읽거나 수정할 수 있습니다.

## 클래스

### `WrappedHttpServletRequest`

`HttpServletRequest`를 래핑하여 요청 본문을 캐싱하고 여러 번 읽을 수 있도록 합니다.

#### 생성자

- `WrappedHttpServletRequest(HttpServletRequest request) throws IOException`:
  제공된 `HttpServletRequest`로부터 요청 본문을 읽고 캐싱하여 새로운 `WrappedHttpServletRequest`를 생성합니다.

#### 메서드

- `ServletInputStream getInputStream()`: 캐시된 요청 본문을 읽기 위한 `ServletInputStream`을 반환합니다.
- `String getBody()`: 캐시된 요청 본문을 `String`으로 반환합니다.

### `WrappedHttpServletResponse`

`HttpServletResponse`를 래핑하여 응답 본문을 캐싱하고 클라이언트에게 전송하기 전에 여러 번 읽거나 수정할 수 있도록 합니다.

#### 생성자

- `WrappedHttpServletResponse(HttpServletResponse response)`:
  내부 버퍼를 사용하여 응답 본문을 캐싱하는 새로운 `WrappedHttpServletResponse`를 생성합니다.

#### 메서드

- `ServletOutputStream getOutputStream()`: 내부 버퍼에 쓰기 위한 `ServletOutputStream`을 반환합니다.
- `void flushBuffer() throws IOException`: 캐시된 응답 본문을 원래 응답의 출력 스트림에 기록하고 플러시합니다.
- `void reset()`: 응답을 리셋하고 버퍼를 비웁니다.
- `void resetBuffer()`: 응답을 리셋하지 않고 버퍼만 비웁니다.
- `String getBody()`: 캐시된 응답 본문을 `String`으로 반환합니다.

## 사용 방법

### 요청 캐싱 예제

요청 본문을 캐싱하기 위해 `WrappedHttpServletRequest`를 필터나 인터셉터에서 래핑하여 사용할 수 있습니다:

```java
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class RequestCachingFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		WrappedHttpServletRequest wrappedRequest = new WrappedHttpServletRequest((HttpServletRequest)request);
		chain.doFilter(wrappedRequest, response);
	}
}
```

### 응답 캐싱 예제

응답 본문을 캐싱하기 위해 `WrappedHttpServletResponse`를 필터나 인터셉터에서 래핑하여 사용할 수 있습니다:

```java
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ResponseCachingFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		WrappedHttpServletResponse wrappedResponse = new WrappedHttpServletResponse((HttpServletResponse)response);
		chain.doFilter(request, wrappedResponse);
		// 응답 본문을 사용할 수 있음
		String responseBody = wrappedResponse.getBody();
		// 필요에 따라 응답 본문을 수정하거나 로그를 기록할 수 있음
	}
}
```

이 문서는 보안 모듈을 Spring Boot 애플리케이션에 통합하여 활용하는 방법을 제공합니다. 각 모듈의 설치, 설정, 동작 방식 및 예시를 참고하여 애플리케이션의 보안을 강화하시기 바랍니다.