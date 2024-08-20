# CustomException 클래스

`CustomException` 클래스는 오류 코드와 함께 사용자 정의 예외를 생성하여 예외 처리 시 보다 구체적인 오류 정보를 제공합니다.

## 클래스 설명

- **설명**: 오류 코드와 함께 사용자 정의 예외를 생성합니다.
- **어노테이션**:
    - `@Getter`: Lombok을 사용하여 `getErrorCode()` 메서드를 자동으로 생성합니다.

## 필드

- `private final ErrorCode errorCode`: 오류 코드 필드. 예외 발생 시 오류 코드 정보를 저장합니다.

## 생성자

### `CustomException(ErrorCode errorCode)`

- **설명**: 오류 코드와 함께 사용자 정의 예외를 생성합니다.
- **매개변수**:
    - `ErrorCode errorCode`: 발생한 오류를 나타내는 오류 코드.
- **동작**:
    - `super("Error occurred with code: " + errorCode)`: 예외 메시지에 오류 코드 정보를 포함하여 예외 객체를 생성합니다.
    - `this.errorCode = errorCode`: 생성자에서 오류 코드를 초기화합니다.

## 참고

- **`ErrorCode`**: 오류 코드를 정의하는 열거형 또는 클래스입니다.

---

# ErrorCode 열거형

`ErrorCode` 열거형은 에러 코드를 정의하고, 각 에러 코드에 대해 HTTP 상태 코드와 메시지를 포함하여 에러 처리를 통합적으로 관리합니다.

## 클래스 설명

- **설명**: 에러 코드와 관련된 상세 정보를 제공합니다.
- **어노테이션**:
    - `@Getter`: Lombok을 사용하여 필드의 getter 메서드를 자동으로 생성합니다.
    - `@RequiredArgsConstructor`: 필수 필드를 포함하는 생성자를 자동으로 생성합니다.

## 열거형 상수

- **`INVALID_CREDENTIALS`**: 잘못된 자격 증명
    - **메시지**: "잘못된 자격 증명입니다"
    - **HTTP 상태**: `HttpStatus.UNAUTHORIZED`

- **`TOKEN_INVALID`**: 유효하지 않은 토큰
    - **메시지**: "토큰이 유효하지 않습니다"
    - **HTTP 상태**: `HttpStatus.FORBIDDEN`

- **`REDIS_ERROR`**: Redis 오류
    - **메시지**: "Redis와의 상호작용 중 오류가 발생했습니다"
    - **HTTP 상태**: `HttpStatus.INTERNAL_SERVER_ERROR`

- **`BAD_REQUEST`**: 잘못된 요청
    - **메시지**: "잘못된 요청입니다"
    - **HTTP 상태**: `HttpStatus.BAD_REQUEST`

- **`NOT_FOUND`**: 리소스 없음
    - **메시지**: "리소스를 찾을 수 없습니다"
    - **HTTP 상태**: `HttpStatus.NOT_FOUND`

- **`INTERNAL_SERVER_ERROR`**: 서버 내부 오류
    - **메시지**: "서버 내부 오류가 발생했습니다"
    - **HTTP 상태**: `HttpStatus.INTERNAL_SERVER_ERROR`

- **`CONTINUE`**: 계속 진행
    - **메시지**: "계속 진행하십시오"
    - **HTTP 상태**: `HttpStatus.CONTINUE`

- **`CREATED`**: 자원 생성 성공
    - **메시지**: "자원이 성공적으로 생성되었습니다"
    - **HTTP 상태**: `HttpStatus.CREATED`

- **`ACCEPTED`**: 요청 수락
    - **메시지**: "요청이 수락되었습니다"
    - **HTTP 상태**: `HttpStatus.ACCEPTED`

- **`NO_CONTENT`**: 내용 없음
    - **메시지**: "내용 없음"
    - **HTTP 상태**: `HttpStatus.NO_CONTENT`

- **`MOVED_PERMANENTLY`**: 영구 이동
    - **메시지**: "영구적으로 이동되었습니다"
    - **HTTP 상태**: `HttpStatus.MOVED_PERMANENTLY`

- **`FORBIDDEN`**: 금지된 접근
    - **메시지**: "금지된 접근입니다"
    - **HTTP 상태**: `HttpStatus.FORBIDDEN`

- **`METHOD_NOT_ALLOWED`**: 허용되지 않은 메서드
    - **메시지**: "허용되지 않은 메서드입니다"
    - **HTTP 상태**: `HttpStatus.METHOD_NOT_ALLOWED`

- **`UNAUTHORIZED`**: 인증되지 않음
    - **메시지**: "인증되지 않았습니다"
    - **HTTP 상태**: `HttpStatus.UNAUTHORIZED`

- **`REQUEST_TIMEOUT`**: 요청 시간 초과
    - **메시지**: "요청 시간 초과"
    - **HTTP 상태**: `HttpStatus.REQUEST_TIMEOUT`

- **`SERVICE_UNAVAILABLE`**: 서비스 이용 불가
    - **메시지**: "서비스를 사용할 수 없습니다"
    - **HTTP 상태**: `HttpStatus.SERVICE_UNAVAILABLE`

## 필드

- `private final String code`: 에러 코드
- `private final String message`: 에러 메시지
- `private final HttpStatus httpStatus`: HTTP 상태 코드
