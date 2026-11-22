# HTTP Reverse Proxy / HTTP 리버스 프록시

[ENG](#eng) | [KR](#kr)

## ENG

A Spring MVC proxy that forwards HTTP requests to one configured upstream server with RestTemplate. It keeps the method, path, query, body bytes, response status, and repeated end-to-end headers.

### Project structure

```text
pom.xml
src/
├── main/
│   ├── java/io/github/parkwoong/demo/
│   │   ├── DemoApplication.java
│   │   └── ProxyController.java
│   └── resources/
│       └── application.properties
└── test/java/io/github/parkwoong/demo/
    └── ProxyControllerTest.java
```

Java package: `io.github.parkwoong.demo`.

| File | Role |
|---|---|
| [DemoApplication.java](src/main/java/io/github/parkwoong/demo/DemoApplication.java) | Starts Spring Boot and runs the example. |
| [ProxyController.java](src/main/java/io/github/parkwoong/demo/ProxyController.java) | Builds the upstream URI, filters connection-specific headers, forwards the request, and returns the response. |
| [application.properties](src/main/resources/application.properties) | Holds the demo settings. |
| [pom.xml](pom.xml) | Dependencies and build settings. |

### How it works

Client → ProxyController → configured upstream → client. The proxy removes hop-by-hop headers and lets the HTTP client set Host and Content-Length. Upstream 4xx/5xx responses keep their status and body. Connection or read failures return 502. Redirects are returned without being followed.

### Run

Requirements: Java 17+ and Maven 3.6.3+. Run these commands from the repository root.

```sh
mvn clean verify
mvn spring-boot:run
```

The proxy listens on 127.0.0.1:8080. Its default upstream is http://localhost:9090. Set PROXY_UPSTREAM to change it. Connect timeout is 3 seconds; read timeout is 5 seconds.

In another terminal, start a sample upstream in a folder containing only public test files:

```sh
python3 -m http.server 9090 --bind 127.0.0.1
```

Then call the proxy from another terminal:

```sh
curl -i http://localhost:8080/
```

The response should be HTTP 200 with the upstream directory listing or index page.

### Tests

ProxyControllerTest uses a local HTTP server to check POST, encoded query text, Korean body text, status 422, and repeated Set-Cookie headers. It also checks header filtering.

[ProxyControllerTest.java](src/test/java/io/github/parkwoong/demo/ProxyControllerTest.java)

### Scope and limits

Supports GET, HEAD, POST, PUT, DELETE, and OPTIONS. PATCH, WebSocket, streaming, authentication, and rate limiting are not included. Bodies are held in memory. Use only a trusted upstream and small local examples.

This learning example uses Spring Boot 3.5.16. Before production use, move to a supported version; see the [3.5 OSS support notice](https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/). Production connections and load tests are not included.

Adapted from a Java proxy project using generic names and sample data. Original Git history, production settings, and business identifiers are not included.

## KR

Spring MVC와 RestTemplate으로 지정된 upstream 서버에 HTTP 요청을 전달합니다. 메서드·경로·쿼리·본문 바이트·응답 상태와 반복되는 종단 간 헤더를 유지합니다.

### 프로젝트 구조

```text
pom.xml
src/
├── main/
│   ├── java/io/github/parkwoong/demo/
│   │   ├── DemoApplication.java
│   │   └── ProxyController.java
│   └── resources/
│       └── application.properties
└── test/java/io/github/parkwoong/demo/
    └── ProxyControllerTest.java
```

Java 패키지: `io.github.parkwoong.demo`.

| 파일 | 역할 |
|---|---|
| [DemoApplication.java](src/main/java/io/github/parkwoong/demo/DemoApplication.java) | Spring Boot를 시작하고 예제를 실행합니다. |
| [ProxyController.java](src/main/java/io/github/parkwoong/demo/ProxyController.java) | upstream URI를 만들고 연결 전용 헤더를 제거한 뒤 요청과 응답을 전달합니다. |
| [application.properties](src/main/resources/application.properties) | 예제 실행 설정을 정의합니다. |
| [pom.xml](pom.xml) | 의존성과 빌드 설정입니다. |

### 동작 방식

클라이언트 → ProxyController → 설정된 upstream → 클라이언트 순서로 동작합니다. 연결 구간 전용 헤더를 제거하고 Host와 Content-Length는 HTTP 클라이언트가 설정하게 합니다. upstream의 4xx·5xx 상태와 본문은 유지하고 연결·읽기 오류는 502로 응답합니다. 리다이렉트는 따라가지 않고 그대로 반환합니다.

### 실행 방법

Java 17 이상, Maven 3.6.3 이상이 필요합니다. 저장소 루트에서 실행하세요.

```sh
mvn clean verify
mvn spring-boot:run
```

프록시는 127.0.0.1:8080에서 실행됩니다. 기본 upstream은 http://localhost:9090이며 PROXY_UPSTREAM 환경변수로 변경할 수 있습니다. 연결 타임아웃은 3초, 읽기 타임아웃은 5초입니다.

다른 터미널에서 공개 가능한 테스트 파일만 있는 폴더로 이동한 뒤 샘플 upstream을 실행하세요:

```sh
python3 -m http.server 9090 --bind 127.0.0.1
```

다른 터미널에서 프록시를 호출하세요:

```sh
curl -i http://localhost:8080/
```

upstream의 디렉터리 목록 또는 인덱스 페이지가 HTTP 200 응답으로 반환됩니다.

### 테스트

ProxyControllerTest는 로컬 HTTP 서버로 POST, 인코딩된 쿼리, 한글 본문, 422 상태 및 반복 Set-Cookie 헤더를 확인합니다. 헤더 제거 동작도 검사합니다.

[ProxyControllerTest.java](src/test/java/io/github/parkwoong/demo/ProxyControllerTest.java)

### 범위와 제한

GET·HEAD·POST·PUT·DELETE·OPTIONS를 지원합니다. PATCH, WebSocket, 스트리밍, 인증 및 요청 속도 제한은 포함하지 않습니다. 본문은 메모리에 보관하므로 신뢰할 수 있는 upstream과 소규모 로컬 실습에 사용하세요.

Spring Boot 3.5.16 기반 학습용 예제입니다. 운영 도입 전 지원 버전으로 전환하세요. [3.5 OSS 지원 종료 안내](https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/)를 참고할 수 있습니다. 운영 시스템 연결 및 부하 테스트는 포함하지 않습니다.

기존 Java 프록시 프로젝트에서 기술을 추출해 범용 이름과 샘플 데이터로 재작성했습니다. 기존 Git 이력, 운영 설정 및 업무 식별자는 포함하지 않습니다.
