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
    enabled: ${XSS_FILTER_ENABLED:true} # 환경 변수로 동적 설정 가능
    patterns:
      - "<iframe.*?>.*?</iframe>"
      - "<object.*?>.*?</object>"
      - "<embed.*?>"

```