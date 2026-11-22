# HTTP 리버스 프록시

Spring MVC + RestTemplate으로 고정 upstream에 메서드·경로·쿼리·바이트 본문을 전달합니다. 원본의 업무별 우회 정책은 제외하고, 응답 상태와 반복 헤더를 유지합니다.

기존 Java 프록시 프로젝트에서 기술 주제를 추출하고 클래스·변수·패키지·데이터를 범용 예제로 재작성했습니다. 기존 Git 이력, 운영 설정, 업무 식별자는 포함하지 않습니다.

## 실행

Java 17 이상, Maven 3.6.3 이상이 필요합니다.

```sh
mvn clean verify
mvn spring-boot:run
```

Spring Boot 3.5.16 기반 학습용 예제입니다. [3.5 계열 OSS 지원 종료 안내](https://spring.io/blog/2026/06/25/spring-boot-3-5-16-available-now/)를 참고하고 운영 도입 시 지원 버전으로 전환하세요.

## 로컬 실습

별도 터미널에서 `python3 -m http.server 9090 --bind 127.0.0.1`로 샘플 upstream을 실행한 다음 `curl -i http://localhost:8080/`로 호출합니다. `PROXY_UPSTREAM` 환경변수는 관리자가 신뢰하는 서버 주소로만 지정하세요.

범위: GET/HEAD/POST/PUT/DELETE/OPTIONS. PATCH, WebSocket, 스트리밍, 운영용 인증·트래픽 제한은 구현하지 않았습니다. 본문은 메모리에 버퍼링되므로 소규모 로컬 학습용입니다. 자동 redirect는 비활성화하고 upstream의 3xx를 그대로 반환합니다.

## 검증 범위

`mvn verify`로 단위/통합 테스트와 실행 JAR 패키징을 확인할 수 있습니다. 외부 운영 시스템 연결 및 부하 테스트는 포함하지 않습니다.
