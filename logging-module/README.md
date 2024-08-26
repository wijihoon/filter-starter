# Logging Module

`logging-module`은 Spring Boot 애플리케이션에서 로그를 효율적으로 처리하고 전송할 수 있는 모듈입니다. 이 모듈은 Kafka와 Loki를 통해 로그를 전송할 수 있으며, 로그 필터와 설정 관리
기능도 제공합니다.

## 기능

- **로그 필터링**: HTTP 요청과 응답에서 로그를 필터링하여 전송합니다.
- **로그 전송**: Kafka와 Loki를 통해 로그를 전송할 수 있습니다.
- **설정 관리**: 로그 전송과 관련된 다양한 설정을 관리할 수 있습니다.

## 의존성

이 모듈을 사용하려면 `build.gradle` 또는 `pom.xml`에 다음과 같은 의존성을 추가해야 합니다.

### Gradle

```groovy
dependencies {
    implementation 'com.example:logging-module:1.0.0'
}
```

```Maven
<dependency>
    <groupId>com.example</groupId>
    <artifactId>logging-module</artifactId>
    <version>1.0.0</version>
</dependency>   
```

## 로그 설정

이 모듈은 로그 설정을 외부 구성 파일에서 읽어옵니다. `LogProperties` 클래스는 다음과 같은 로그 설정을 지원합니다.

### 기본 설정

- **요청 본문 설정**
    - `requestBody.truncate`: 요청 본문을 잘라낼지 여부를 설정합니다. 기본값은 `false`입니다.

- **응답 본문 설정**
    - `responseBody.truncate`: 응답 본문을 잘라낼지 여부를 설정합니다. 기본값은 `false`입니다.

- **로그 본문 설정**
    - `body.maxSize`: 로그 본문의 최대 크기를 바이트 단위로 설정합니다. 기본값은 `1024` 바이트입니다.

- **로그 전송 방식**
    - `logDestination`: 로그 전송 방식을 설정합니다. `"kafka"` 또는 `"loki"` 값을 사용할 수 있으며, 기본값은 `"kafka"`입니다.

- **민감 정보 필드 목록**
    - `sensitiveFields`: 로그에서 마스킹할 민감 정보 필드의 목록을 설정합니다. 기본값으로 `"password"`와 `"cardNumber"`가 설정되어 있습니다.

### 설정 예시

`application.properties` 또는 `application.yml` 파일에서 다음과 같이 설정할 수 있습니다:

```properties
# 로그 필터 활성화 여부
log.enabled=true
# 요청 본문을 잘라낼지 여부
log.requestBody.truncate=true
# 응답 본문을 잘라낼지 여부
log.responseBody.truncate=true
# 로그 본문의 최대 크기 (바이트 단위)
log.body.maxSize=2048
# 로그 전송 방식 (kafka 또는 loki)
log.logDestination=loki
# 마스킹할 민감 정보 필드 목록
log.sensitiveFields=password,cardNumber,ssn
```

```yaml
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
```

## Kafka 설정

이 모듈은 Kafka를 로그 전송 방식으로 지원하며, 다음과 같은 설정을 통해 Kafka와의 연결을 구성할 수 있습니다.

### Kafka 설정 속성

- **Kafka 서버의 주소 목록**
    - `kafka.bootstrapServers`: Kafka 클러스터의 서버 주소 목록을 설정합니다. (예: `localhost:9092`)
    - 이 속성은 필수로 설정되어야 하며, null일 수 없습니다.

- **Kafka 주제 (토픽) 이름**
    - `kafka.topicName`: 로그를 전송할 Kafka 토픽의 이름을 설정합니다.
    - 이 속성은 필수로 설정되어야 하며, null일 수 없습니다.

- **메시지 전송 성공 응답을 기다리는 설정**
    - `kafka.acks`: 메시지 전송 성공 응답을 기다리는 설정입니다. (기본값: `"1"`)
        - `"all"` - 모든 복제본에서 성공 응답을 기다립니다.
        - `"1"` - 리더에서만 성공 응답을 기다립니다.
        - `"0"` - 성공 응답을 기다리지 않습니다.

- **메시지 전송 실패 시 재시도 횟수**
    - `kafka.retries`: 메시지 전송 실패 시 재시도 횟수를 설정합니다. (기본값: `0`)

- **메시지 배치 크기**
    - `kafka.batchSize`: 메시지 배치 크기를 바이트 단위로 설정합니다. (기본값: `16384`)

- **메시지 전송 지연 시간**
    - `kafka.lingerMs`: 메시지 전송 지연 시간을 밀리초 단위로 설정합니다. (기본값: `0`)

- **메시지 압축 타입**
    - `kafka.compressionType`: 메시지 압축 타입을 설정합니다. (기본값: `"none"`)
        - `"none"` - 압축 없음
        - `"gzip"` - GZIP 압축
        - `"snappy"` - Snappy 압축
        - `"lz4"` - LZ4 압축

- **메시지 키와 값을 직렬화하기 위한 설정**
    - `kafka.keySerializer`: 메시지 키를 직렬화하기 위한 클래스 이름입니다. 기본값은 `org.apache.kafka.common.serialization.StringSerializer`
      입니다.
    - `kafka.valueSerializer`: 메시지 값을 직렬화하기 위한 클래스 이름입니다. 기본값은 `org.apache.kafka.common.serialization.StringSerializer`
      입니다.

### 설정 예시

`application.properties` 또는 `application.yml` 파일에서 다음과 같이 설정할 수 있습니다:

```properties
# Kafka 서버의 주소 목록
kafka.bootstrapServers=localhost:9092
# Kafka 주제 (토픽) 이름
kafka.topicName=my-log-topic
# 메시지 전송 성공 응답을 기다리는 설정
kafka.acks=all
# 메시지 전송 실패 시 재시도 횟수
kafka.retries=3
# 메시지 배치 크기 (바이트 단위)
kafka.batchSize=16384
# 메시지 전송 지연 시간 (밀리초 단위)
kafka.lingerMs=5
# 메시지 압축 타입
kafka.compressionType=gzip
# 메시지 키와 값을 직렬화하기 위한 설정
kafka.keySerializer=org.apache.kafka.common.serialization.StringSerializer
kafka.valueSerializer=org.apache.kafka.common.serialization.StringSerializer
```

```yaml
kafka:
  # Kafka 서버의 주소 목록
  bootstrapServers: localhost:9092

  # Kafka 주제 (토픽) 이름
  topicName: my-log-topic

  # 메시지 전송 성공 응답을 기다리는 설정
  acks: all

  # 메시지 전송 실패 시 재시도 횟수
  retries: 3

  # 메시지 배치 크기 (바이트 단위)
  batchSize: 16384

  # 메시지 전송 지연 시간 (밀리초 단위)
  lingerMs: 5

  # 메시지 압축 타입
  compressionType: gzip

  # 메시지 키와 값을 직렬화하기 위한 설정
  keySerializer: org.apache.kafka.common.serialization.StringSerializer
  valueSerializer: org.apache.kafka.common.serialization.StringSerializer
```

## Loki 설정

Loki 설정 클래스는 Loki 서버와의 연결을 위한 구성 값을 정의합니다. 이 설정은 애플리케이션의 외부 구성 파일에서 읽어와 사용됩니다. 아래는 `LokiProperties` 클래스의 각 설정 항목에 대한
설명입니다.

## 설정 항목

### `url`

- **설명**: Loki 서버의 URL입니다. 이 속성은 필수로 설정되어야 하며, 빈 값이거나 null일 수 없습니다.
- **형식**: 문자열
- **예시**:

```properties
# Loki 서버의 URL을 설정합니다.
loki.url=http://localhost:3100
```

```yaml
loki:
  url: http://localhost:3100
```

## 요청(Request) 로그 예시

```json
{
  "traceId": "e7f94a7d-6b9f-4b2a-9bdc-0b795324f7e9",
  "context": "REQUEST",
  "method": "POST",
  "uri": "/api/v1/user/login",
  "query": "source=mobile",
  "remoteAddress": "192.168.1.100",
  "headers": {
    "Content-Type": "application/json",
    "Authorization": "Bearer abcdef123456",
    "User-Agent": "Mozilla/5.0",
    "Accept": "application/json"
  },
  "body": "{\"username\":\"validUser\",\"password\":\"[PROTECTED]\"}"
}
```

## 응답(Response) 로그 예시

```json
{
  "traceId": "e7f94a7d-6b9f-4b2a-9bdc-0b795324f7e9",
  "context": "RESPONSE",
  "status": 200,
  "headers": {
    "Content-Type": "application/json",
    "Cache-Control": "no-cache",
    "Set-Cookie": "sessionId=abc123xyz; HttpOnly; Secure"
  },
  "body": "{\"status\":\"OK\",\"message\":\"Login successful\",\"data\":{\"username\":\"validUser\",\"token\":\"abc123xyz\"}}"
}
```
