# HTTP Proxy / HTTP 프록시

[ENG](#eng) | [KR](#kr)

## ENG

### Project structure

```text
pom.xml
src/main/java/io/github/parkwoong/demo/DemoApplication.java
src/main/java/io/github/parkwoong/demo/config/PropsConfig.java
src/main/java/io/github/parkwoong/demo/controller/ProxyController.java
src/main/java/io/github/parkwoong/demo/properties/GatewayProperties.java
src/main/java/io/github/parkwoong/demo/spec/util/HttpUtils.java
src/main/resources/application.properties
src/test/java/io/github/parkwoong/demo/ProxyControllerTest.java
```

### Code explanation

`ProxyController.toss` reads headers with `HttpUtils.getHeaders`, builds the target URL with
`getForwardUri`, and forwards the request through `RestTemplate.exchange`. `GatewayProperties`
binds `gateway.origin`; `PropsConfig` registers it.

Upstream HTTP errors keep their status and body. Other failures return `500` with
`Proxy failed`. Bodies use String values. Requests use one value per header, and responses copy
only Content-Type. The HTTP client has no explicit timeout configuration.

### Run and test

Use JDK 8 and Maven 3.6.3+. Set JAVA_HOME to your JDK 8 directory, then run from the repository
root:

```sh
java -version
mvn -version
mvn clean verify
mvn spring-boot:run
```

`ProxyControllerTest` runs a local HTTP server and checks forwarding, query strings, status/body
handling, and HTTP helpers.

Start a sample upstream in a separate terminal, from a folder with only public sample files:

```sh
python3 -m http.server 9090 --bind 127.0.0.1
```

Call the running proxy from another terminal:

```sh
curl -i http://localhost:8080/
```

The response contains the upstream page. PROXY_UPSTREAM changes the destination (default:
http://localhost:9090). Stop with Ctrl+C.

#### Postman API

**Setup**

Run the sample upstream on localhost:9090 as shown above.

##### Forward to the upstream

**Request**

- Method: GET
- URL: http://localhost:8080/
- Body: none

**Response**

- Status: 200 OK
- Body: HTML directory listing from the upstream (depends on the served files).

## KR

### 프로젝트 구조

```text
pom.xml
src/main/java/io/github/parkwoong/demo/DemoApplication.java
src/main/java/io/github/parkwoong/demo/config/PropsConfig.java
src/main/java/io/github/parkwoong/demo/controller/ProxyController.java
src/main/java/io/github/parkwoong/demo/properties/GatewayProperties.java
src/main/java/io/github/parkwoong/demo/spec/util/HttpUtils.java
src/main/resources/application.properties
src/test/java/io/github/parkwoong/demo/ProxyControllerTest.java
```

### 코드 설명

`ProxyController.toss`는 `HttpUtils.getHeaders`로 헤더를 읽고
`getForwardUri`로 대상 URL을 만든 뒤 `RestTemplate.exchange`로 요청을 전달한다.
`GatewayProperties`는 `gateway.origin`을 바인딩하고 `PropsConfig`가 이를 등록한다.

upstream HTTP 오류는 상태와 본문을 유지한다. 그 외 오류는 `500`과 `Proxy failed`를 반환한다.
본문은 String이며 요청 헤더는 이름별 값 하나를 사용한다. 응답 헤더는 Content-Type만 복사하고 HTTP
클라이언트 타임아웃은 명시적으로 설정하지 않는다.

### 실행과 테스트

JDK 8, Maven 3.6.3 이상 사용. 저장소 루트에서 실행.

```sh
java -version
mvn -version
mvn clean verify
mvn spring-boot:run
```

`ProxyControllerTest`는 로컬 HTTP 서버로 요청 전달, 쿼리, 상태·본문 처리 및 HTTP 유틸리티를
확인한다.

공개 가능한 샘플 파일 폴더에서 upstream 실행:

```sh
python3 -m http.server 9090 --bind 127.0.0.1
```

다른 터미널에서 프록시 호출:

```sh
curl -i http://localhost:8080/
```

upstream 페이지가 응답으로 반환된다. PROXY_UPSTREAM으로 대상 주소를 바꿀 수 있으며 기본값은
http://localhost:9090이다. Ctrl+C로 종료한다.

#### Postman API

**준비**

위 실행 방법대로 localhost:9090에 샘플 업스트림 실행.

##### 업스트림으로 전달

**요청**

- 메서드: GET
- URL: http://localhost:8080/
- 본문: 없음

**응답**

- 상태: 200 OK
- 본문: 업스트림 HTML 디렉터리 목록 (파일에 따라 달라짐).
