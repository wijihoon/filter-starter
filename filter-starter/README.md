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
    implementation 'ent.genesisframework:filter-starter:1.0.0'
}
```

```maven
<dependency>
    <groupId>ent.genesisframework</groupId>
    <artifactId>filter-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 예제

다음은 application.properties 또는 application.yml 파일에서 필터를 활성화하거나 설정하는 예제입니다:

```properties
# 로그 모듈 설정
filter.log.enabled=true
filter.log.requestBody.truncate=true
filter.log.responseBody.truncate=true
filter.log.body.maxSize=2048
filter.log.logDestination=loki
filter.log.sensitiveFields[0]=password
filter.log.sensitiveFields[1]=cardNumber
filter.log.sensitiveFields[2]=ssn
# XSS 보호 모듈 설정
filter.xss.enabled=true
filter.xss.patterns[0]=<iframe.*?>.*?</iframe>          # iframe 태그
filter.xss.patterns[1]=<object.*?>.*?</object>          # object 태그
filter.xss.patterns[2]=<embed.*?>                       # embed 태그
filter.xss.patterns[3]=<script.*?>.*?</script>          # script 태그
filter.xss.patterns[4]=<.*?javascript:.*?>              # javascript 프로토콜
filter.xss.patterns[5]=<.*?vbscript:.*?>                # vbscript 프로토콜
filter.xss.patterns[6]=<.*?data:.*?>                    # data URIs
filter.xss.patterns[7]=<.*?expression\\(.*?>            # expressions
filter.xss.patterns[8]=<.*?onload\\s*=.*?>              # onload 이벤트 핸들러
filter.xss.patterns[9]=<.*?onclick\\s*=.*?>             # onclick 이벤트 핸들러
filter.xss.patterns[10]=<.*?onerror\\s*=.*?>            # onerror 이벤트 핸들러
filter.xss.patterns[11]=<.*?onmouseover\\s*=.*?>         # onmouseover 이벤트 핸들러
filter.xss.patterns[12]=<.*?onfocus\\s*=.*?>            # onfocus 이벤트 핸들러
filter.xss.patterns[13]=<.*?onchange\\s*=.*?>           # onchange 이벤트 핸들러
filter.xss.patterns[14]=<.*?oninput\\s*=.*?>            # oninput 이벤트 핸들러
filter.xss.patterns[15]=<.*?onabort\\s*=.*?>            # onabort 이벤트 핸들러
filter.xss.patterns[16]=<.*?onbeforeunload\\s*=.*?>      # onbeforeunload 이벤트 핸들러
filter.xss.patterns[17]=<.*?src\\s*=.*?>                 # src 속성
filter.xss.patterns[18]=<.*?href\\s*=.*?>                # href 속성
filter.xss.patterns[19]=<.*?background\\s*=.*?>          # background 속성
filter.xss.patterns[20]=<style.*?>.*?</style>            # style 태그
filter.xss.patterns[21]=<form.*?>.*?</form>              # form 태그
filter.xss.patterns[22]=<.*?data-.*?>                    # data 속성
# CORS 설정
filter.cors.enabled=false
filter.cors.allowedOrigins[0]=http://example.com
filter.cors.allowedOrigins[1]=http://another-domain.com
filter.cors.allowedMethods[0]=GET
filter.cors.allowedMethods[1]=POST
filter.cors.allowedMethods[2]=PUT
filter.cors.allowedMethods[3]=DELETE
filter.cors.allowedHeaders[0]=Authorization
filter.cors.allowedHeaders[1]=Content-Type
filter.cors.allowCredentials=true
# CSRF 설정
filter.csrf.enabled=true
filter.csrf.headers.xFrameOptions=DENY
filter.csrf.headers.xXssProtection=1; mode=block
filter.csrf.headers.xContentTypeOptions=nosniff
# SQL 인젝션 필터 설정
filter.sql.enabled=true
filter.sql.patterns[0]=.*([';]+|(--)).* # SQL 주석 및 SQL 주입
filter.sql.patterns[1]=.*union.*select.* # UNION SELECT
filter.sql.patterns[2]=.*select.*from.* # SELECT FROM
filter.sql.patterns[3]=.*insert.*into.* # INSERT INTO
filter.sql.patterns[4]=.*update.*set.* # UPDATE SET
filter.sql.patterns[5]=.*delete.*from.* # DELETE FROM

```

```yaml
filter:
  log:
    enabled: true
    # 요청 본문을 잘라낼지 여부
    requestBody:
      truncate: true

    # 응답 본문을 잘라낼지 여부
    responseBody:
      truncate: true

    # 로그 본문의 최대 크기 (바이트 단위)
    body:
      maxSize: 2048

    # 로그 전송 방식 (kafka 또는 loki)
    logDestination: loki

    # 마스킹할 민감 정보 필드 목록
    sensitiveFields:
      - password
      - cardNumber
      - ssn
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
      - "<.*?onclick\\s*=.*?>"             # onclick 이벤트 핸들러
      - "<.*?onerror\\s*=.*?>"             # onerror 이벤트 핸들러
      - "<.*?onmouseover\\s*=.*?>"         # onmouseover 이벤트 핸들러
      - "<.*?onfocus\\s*=.*?>"             # onfocus 이벤트 핸들러
      - "<.*?onchange\\s*=.*?>"            # onchange 이벤트 핸들러
      - "<.*?oninput\\s*=.*?>"             # oninput 이벤트 핸들러
      - "<.*?onabort\\s*=.*?>"             # onabort 이벤트 핸들러
      - "<.*?onbeforeunload\\s*=.*?>"      # onbeforeunload 이벤트 핸들러
      - "<.*?src\\s*=.*?>"                 # src 속성
      - "<.*?href\\s*=.*?>"                # href 속성
      - "<.*?background\\s*=.*?>"          # background 속성
      - "<style.*?>.*?</style>"            # style 태그
      - "<form.*?>.*?</form>"              # form 태그
      - "<.*?data-.*?>"                    # data 속성
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
  csrf:
    enabled: true
    headers:
      xFrameOptions: DENY
      xXssProtection: 1; mode=block
      xContentTypeOptions: nosniff
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
