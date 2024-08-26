# XSS 보호 모듈

이 XSS 보호 모듈은 Spring Boot Starter로, 애플리케이션에서 잠재적인 XSS(교차 사이트 스크립팅) 공격을 손쉽게 필터링하고 차단할 수 있도록 지원합니다. 이 모듈을 의존성으로 추가하면 필터와
관련된 설정이 자동으로 구성됩니다.

## 주요 기능

- **자동 XSS 필터링**: 요청의 파라미터, 쿠키, 본문에서 XSS 위협을 자동으로 감지합니다.
- **패턴 설정 가능**: XSS 공격을 탐지하는 정규식을 손쉽게 커스터마이징할 수 있습니다.
- **유연한 활성화/비활성화 옵션**: 설정을 통해 필터의 활성화 여부를 제어할 수 있습니다.

## 설치 방법

`build.gradle` 파일에 다음 의존성을 추가하세요:

```groovy
dependencies {
    implementation 'com.example:xss-module-starter:1.0.0'
}
```

위의 com.example:xss-module-starter:1.0.0 부분을 실제 모듈의 그룹 ID와 버전으로 교체하세요.

## 설정 방법

모듈을 의존성으로 추가하면 기본적으로 필터가 활성화됩니다. 추가로 설정이 필요하다면 application.yml 또는 application.properties 파일에서 설정을 구성할 수 있습니다.

## 기본 설정

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

## 설정 상세 설명

- **filter.xss.enabled**: XSS 필터의 활성화 여부를 설정합니다. 기본값은 true입니다.
- **filter.xss.patterns**: XSS 공격을 감지하기 위한 정규식 패턴 목록입니다. 애플리케이션의 요구에 따라 패턴을 추가, 제거 또는 수정할 수 있습니다.

## 동작 방식

XSS 필터는 HTTP 요청의 다음 요소들을 검사합니다:

- **요청 파라미터**: 모든 요청 파라미터에서 XSS 공격을 탐지합니다.
- **쿠키**: 쿠키의 이름과 값을 검사하여 악성 스크립트를 감지합니다.
- **요청 본문**: 요청에 본문이 있는 경우, 해당 내용도 검사합니다.
  만약 XSS 패턴이 감지되면 요청은 차단되고 403 Forbidden 응답이 반환됩니다.

## 고급 설정

필터의 동작을 세부적으로 조정하고 싶다면, 아래와 같이 설정 파일을 통해 고급 설정을 적용할 수 있습니다.

예를 들어, 특정 환경에서 필터를 비활성화하거나 추가 패턴을 설정하려면 다음과 같이 구성합니다:

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
```

```properties
# XSS 필터 활성화 여부 설정
filter.xss.enabled=true

# XSS 공격을 탐지할 패턴 리스트 설정
filter.xss.patterns[0]=<iframe.*?>.*?</iframe>  # iframe 태그
filter.xss.patterns[1]=<object.*?>.*?</object>  # object 태그
filter.xss.patterns[2]=<embed.*?>               # embed 태그
filter.xss.patterns[3]=<script.*?>.*?</script>  # script 태그
filter.xss.patterns[4]=<.*?javascript:.*?>      # javascript 프로토콜
filter.xss.patterns[5]=<.*?vbscript:.*?>        # vbscript 프로토콜
filter.xss.patterns[6]=<.*?data:.*?>            # data URIs
filter.xss.patterns[7]=<.*?expression\\(.*?>    # expressions
filter.xss.patterns[8]=<.*?onload\\s*=.*?>      # onload 이벤트 핸들러
filter.xss.patterns[9]=<.*?onclick\\s*=.*?>     # onclick 이벤트 핸들러
filter.xss.patterns[10]=<.*?onerror\\s*=.*?>    # onerror 이벤트 핸들러
filter.xss.patterns[11]=<.*?onmouseover\\s*=.*?># onmouseover 이벤트 핸들러
filter.xss.patterns[12]=<.*?onfocus\\s*=.*?>    # onfocus 이벤트 핸들러
filter.xss.patterns[13]=<.*?onchange\\s*=.*?>   # onchange 이벤트 핸들러
filter.xss.patterns[14]=<.*?oninput\\s*=.*?>    # oninput 이벤트 핸들러
filter.xss.patterns[15]=<.*?onabort\\s*=.*?>    # onabort 이벤트 핸들러
filter.xss.patterns[16]=<.*?onbeforeunload\\s*=.*?> # onbeforeunload 이벤트 핸들러
filter.xss.patterns[17]=<.*?src\\s*=.*?>         # src 속성
filter.xss.patterns[18]=<.*?href\\s*=.*?>        # href 속성
filter.xss.patterns[19]=<.*?background\\s*=.*?>  # background 속성
filter.xss.patterns[20]=<style.*?>.*?</style>    # style 태그
filter.xss.patterns[21]=<form.*?>.*?</form>      # form 태그
filter.xss.patterns[22]=<.*?data-.*?>            # data 속성
```

## 요청 / 응답 예시

XSS 공격이 포함된 요청과 응답 예시 요청:

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

응답:

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

XSS 공격이 포함된 URL 파라미터 요청과 응답 예시 요청:

```http
GET /api/search?query=<script>alert('XSS');</script> HTTP/1.1
Host: example.com
```

응답:

```http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
    "success": false,
    "message": "XSS detected in request parameter",
    "data": null,
    "status": "FORBIDDEN"
}
```

XSS 공격이 포함된 쿠키 요청과 응답 예시 요청:

```http
GET /api/user/profile HTTP/1.1
Host: example.com
Cookie: sessionId=<script>alert('XSS');</script>
```

응답:

```http
HTTP/1.1 403 Forbidden
Content-Type: application/json

{
    "success": false,
    "message": "XSS detected in cookie",
    "data": null,
    "status": "FORBIDDEN"
}
```
