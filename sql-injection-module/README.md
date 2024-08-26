# SQL 인젝션 필터 모듈

이 모듈은 SQL 인젝션 공격을 탐지하고 방지하기 위한 필터를 제공합니다. Spring Boot 애플리케이션에 통합하여 사용하며, SQL 인젝션 공격으로부터 애플리케이션을 보호합니다.

## 주요 클래스

- **`SQLInjectionAutoConfiguration`**: SQL 인젝션 필터를 자동으로 구성하고 등록하는 설정 클래스입니다.
- **`SQLInjectionFilter`**: HTTP 요청의 파라미터, 본문 및 쿠키에서 SQL 인젝션 패턴을 검사합니다.
- **`SQLInjectionProperties`**: SQL 인젝션 패턴을 정의하는 설정 클래스입니다.

## 설정 방법

1. **의존성 추가**

   먼저, `build.gradle` 파일에 이 모듈을 의존성으로 추가합니다.

```groovy
dependencies {
    implementation 'shinhancard:sql-module:1.0.0' // 최신 버전으로 변경해 주세요.
}
```

## 설정 파일

```yaml
# application.yml 예시
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

```properties
# application.properties 예시
filter.sql.enabled=true
filter.sql.patterns[0]=.*([';]+|(--)).* # SQL 주석 및 SQL 주입
filter.sql.patterns[1]=.*union.*select.* # UNION SELECT
filter.sql.patterns[2]=.*select.*from.* # SELECT FROM
filter.sql.patterns[3]=.*insert.*into.* # INSERT INTO
filter.sql.patterns[4]=.*update.*set.* # UPDATE SET
filter.sql.patterns[5]=.*delete.*from.* # DELETE FROM
```

patterns는 SQL 인젝션 공격을 탐지하기 위한 정규 표현식 패턴의 리스트입니다. 기본 패턴을 사용하거나, 필요한 패턴으로 수정할 수 있습니다.

## 3. 자동 구성 활성화

`SQLInjectionAutoConfiguration` 클래스는 `filter.sql.enabled` 프로퍼티가 `true`로 설정된 경우에만 활성화됩니다. 기본적으로 이 프로퍼티는 `true`로 설정되어 있으며,
따라서 별도의 설정 없이도 자동으로 활성화됩니다.

자동 구성을 통해 SQL 인젝션 필터가 다음과 같은 조건에서 활성화됩니다:

- **설정 파일에서의 활성화**: `application.yml` 또는 `application.properties` 파일에서 `filter.sql.enabled` 값을 `true`로 설정합니다.
- **기본값 사용**: 프로퍼티가 명시되지 않으면 기본적으로 `true`로 설정됩니다.

## 4. 필터 동작

SQL 인젝션 필터는 HTTP 요청의 다음 요소를 검사합니다:

- **파라미터 검사**: HTTP 요청의 파라미터에서 SQL 인젝션 패턴을 검사합니다.
- **본문 검사**: HTTP 요청의 본문에서 SQL 인젝션 패턴을 검사합니다.
- **쿠키 검사**: HTTP 요청의 쿠키에서 SQL 인젝션 패턴을 검사합니다.

필터는 요청에서 SQL 인젝션 패턴을 발견하면 다음과 같은 방식으로 동작합니다:

1. **파라미터 검사**: 요청 파라미터의 값이 SQL 인젝션 패턴과 일치하면 경고 메시지를 로그에 기록하고 `ServletException`을 발생시킵니다.
2. **본문 검사**: 요청 본문이 SQL 인젝션 패턴과 일치하면 경고 메시지를 로그에 기록하고 `ServletException`을 발생시킵니다.
3. **쿠키 검사**: 쿠키 값이 SQL 인젝션 패턴과 일치하면 경고 메시지를 로그에 기록하고 `ServletException`을 발생시킵니다.

이 필터는 SQL 인젝션 공격을 방지하기 위해 필터 체인에서 요청을 처리하는 동안 위의 검사 과정을 수행합니다.

## 5. 예외 처리

SQL 인젝션 필터가 SQL 인젝션을 감지한 경우, 다음과 같은 방식으로 예외를 처리합니다:

- **예외 발생**: SQL 인젝션 패턴이 발견되면 `ServletException`을 발생시킵니다.
- **응답 코드**: 클라이언트에게 `400 Bad Request` 응답 코드가 반환됩니다.
- **로그 기록**: 감지된 SQL 인젝션에 대한 경고 메시지가 로그에 기록됩니다. 로그 메시지에는 감지된 SQL 인젝션의 위치와 관련된 정보가 포함됩니다.

필터가 SQL 인젝션을 감지할 경우, 애플리케이션은 요청을 처리하지 않고 오류 응답을 반환합니다. 이는 보안 위협을 방지하고, 악성 요청이 시스템에 영향을 미치지 않도록 합니다.

## 요청 / 응답 예시

SQL 인젝션 공격이 포함된 요청과 응답 예시 요청:

```http
POST /api/user/login HTTP/1.1
Host: example.com
Content-Type: application/json

{
    "username": "admin' OR '1'='1",
    "password": "password"
}
```

응답:

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

SQL 인젝션 공격이 포함된 URL 파라미터 요청과 응답 예시 요청:

```http
GET /api/user/search?query=admin' OR '1'='1 HTTP/1.1
Host: example.com
```

응답:

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
    "success": false,
    "message": "SQL Injection detected in request parameter",
    "data": null,
    "status": "BAD_REQUEST"
}
```

SQL 인젝션 공격이 포함된 쿠키 요청과 응답 예시 요청:

```http
GET /api/user/profile HTTP/1.1
Host: example.com
Cookie: sessionId=' OR '1'='1
```

응답:

```http
HTTP/1.1 400 Bad Request
Content-Type: application/json

{
    "success": false,
    "message": "SQL Injection detected in cookie",
    "data": null,
    "status": "BAD_REQUEST"
}
```