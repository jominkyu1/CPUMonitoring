# 프로젝트 개요

서버의 시스템 CPU 사용량을 일/시/분 단위로 조회할 수 있는 API 프로젝트입니다.

# 프로젝트 구성 및 환경

- **Java**: 11
- **Spring Boot**: 2.6.2
- **Database**:
  - **H2 Database**: 1.4.200 (테스트)
  - **MariaDB**: 11.3.2 (운용)

# 의존성 라이브러리

- **Springfox Swagger**: 3.0
- **Spring Boot Actuator**
- **Spring Boot Data JPA**
- **Spring Boot Web**
- **H2 Database**
- **MariaDB**
- **Project Lombok**

# 프로젝트 설정 및 실행 방법

1. **Lombok 설정**: 
    - 최초 실행 시 Lombok 어노테이션 사용을 위한 설정이 필요합니다 (IntelliJ 기준).
    - 아래 이미지를 참고하세요.
   
    ![lombok1](https://github.com/jominkyu1/CPUMonitoring/assets/18455743/1891e6bf-5a14-474e-b2dd-adbbe4ba9664)
    ![lombok2](https://github.com/jominkyu1/CPUMonitoring/assets/18455743/c51a8f94-4c36-4fea-8891-78373615411d)

2. **Datasource 설정**:
    - `application.properties` 파일의 Datasource를 본인의 H2 계정에 맞게 수정합니다.
    - 아래 이미지를 참고하세요.

    ![property](https://github.com/jominkyu1/CPUMonitoring/assets/18455743/db93c383-f697-4485-94f1-453900937f40)

    - 운용 환경의 계정 정보는 환경 변수로 제공합니다.

3. **H2 콘솔 접근**:
    - 테스트 환경에서 `http://localhost:8080/h2-console` 주소로 H2 콘솔에 접근 가능합니다.

4. **운용 환경 실행**:
    - 운용 환경으로 실행하고자 하는 경우 `application-release.properties`를 사용합니다.
    - 데이터베이스 계정과 비밀번호를 각각 `mariausername`, `mariapassword`에 할당합니다.
    - 실행 예시:
    ```sh
    java -jar 파일명.jar --spring.profiles.active=release --mariausername=아이디 --mariapassword=비밀번호
    ```

# 프로젝트 요구사항

1. **CPU 사용률 데이터화**:
    - CPU 사용률을 분 단위로 데이터베이스에 저장합니다.
    - 스프링 액추에이터에서 제공하는 메트릭을 사용하여 분 단위로 수집하여 데이터베이스에 저장합니다.
    - 스프링의 스케줄 기능을 사용하여 매 분마다 분 단위 기록, 매 시간마다 직전 한 시간 동안의 분 단위를 취합하여 시간별 기록, 매일 00시마다 작일 모든 시간을 취합하여 기록합니다.
    - 검색 성능을 위해 일/시/분 단위 각각 테이블을 구성합니다.

2. **분 단위 API**:
    - 지정된 날짜와 시간 구간의 CPU 사용률을 조회합니다.
    - 최근 1주일까지만 제공합니다.

3. **시 단위 API**:
    - 지정된 날짜의 시간 단위 최소/최대/평균 CPU 사용률을 조회합니다.
    - 최근 3개월까지만 제공합니다.

4. **일 단위 API**:
    - 지정된 날짜 구간의 최소/최대/평균 CPU 사용률을 조회합니다.
    - 최근 1년까지만 제공합니다.

5. **조회 공통**:
    - 각 조회시 발생할 수 있는 여러 예외를 공통으로 처리 하였습니다.

6. **테스트**:
    - Rest Controller와 CPU 사용량 스케줄 기능테스트를 위해 가짜객체를 이용한 테스트를 진행 하였습니다. 특히 스케줄 테스트는 실제 분/시/일단위 기록을 기다릴 수 없기에 실제 저장이 이루어 지는가만 테스트한 후, 통합 테스트를 진행 하였습니다.
# API 명세

`http://localhost:8080/swagger-ui/` 주소를 통해 Swagger API 명세를 확인할 수 있습니다.

![swaggerui](https://github.com/jominkyu1/CPUMonitoring/assets/18455743/86512485-fe34-4306-b9af-5a3e9928bf62)

## 분 단위 조회

- **URL**: `/api/cpu-usage/minute`
- **Method**: `GET`
- **설명**: 지정한 시간 구간의 분 단위 CPU 사용량을 조회합니다.
- **Parameters**:
  - `from` (필수): 조회 시작 시간 (ISO 8601 형식)
  - `to` (필수): 조회 종료 시간 (ISO 8601 형식)
- **Response Content-Type**: `application/json`
- **Body**: CPU 사용량 데이터 리스트

### [응답 성공 예시]

- **HTTP Status**: 200 OK
```json
[
    {
        "id": 2,
        "cpuUsage": 1.42,
        "timestamp": "2024-05-23T05:34:00"
    },
    {
        "id": 3,
        "cpuUsage": 0.88,
        "timestamp": "2024-05-23T05:35:00"
    },
    {
        "id": 4,
        "cpuUsage": 0.56,
        "timestamp": "2024-05-23T05:57:00"
    }
]
```

### [응답 실패 예시]

- **HTTP Status**: 400 BadRequest
```json
{
    "code": "400",
    "message": "값이 잘못되었거나 제공하지 않는 기간입니다."
}
```


## 시 단위 조회

- **URL**: `/api/cpu-usage/hour`
- **Method**: `GET`
- **설명**: 지정한 날짜의 시간 단위 CPU 최소/평균/최대 사용량을 조회합니다.
- **Parameters**:
  - `date` (필수): 조회 날짜 (ISO 8601 형식)
- **Response Content-Type**: `application/json`
- **Body**: CPU 사용량 통계 (최소, 최대, 평균)

### [응답 성공 예시]

- **HTTP Status**: 200 OK
```json
[
    {
        "id": 1,
        "minCpuUsage": 0.3,
        "maxCpuUsage": 1.87,
        "avgCpuUsage": 0.69,
        "day": "2024-05-23",
        "time": "07:00:00"
    },
    {
        "id": 2,
        "minCpuUsage": 0.3,
        "maxCpuUsage": 1.87,
        "avgCpuUsage": 0.69,
        "day": "2024-05-23",
        "time": "08:00:00"
    },
    {
        "id": 3,
        "minCpuUsage": 0.3,
        "maxCpuUsage": 1.87,
        "avgCpuUsage": 0.69,
        "day": "2024-05-23",
        "time": "09:00:00"
    }
]
```

### [응답 실패 예시]

- **HTTP Status**: 400 BadRequest
```json
{
    "code": "400",
    "message": "필요한 매개변수가 없습니다."
}
```



## 일 단위 조회

- **URL**: `/api/cpu-usage/day`
- **Method**: `GET`
- **설명**: 지정한 날짜 구간의 일 단위 CPU 최소/최대/평균 사용량을 조회합니다.
- **Parameters**:
  - `from` (필수): 조회 시작 날짜 (ISO 8601 형식)
  - `to` (필수): 조회 종료 날짜 (ISO 8601 형식)
- **Response Content-Type**: `application/json`
- **Body**: CPU 사용량 통계 (최소, 최대, 평균)

### [응답 성공 예시]

- **HTTP Status**: 200 OK
```json
[
    {
        "id": 1,
        "minCpuUsage": 0.3,
        "maxCpuUsage": 1.87,
        "avgCpuUsage": 0.69,
        "day": "2024-05-20",
    },
    {
        "id": 2,
        "minCpuUsage": 0.3,
        "maxCpuUsage": 1.87,
        "avgCpuUsage": 0.69,
        "day": "2024-05-21",
    },
    {
        "id": 3,
        "minCpuUsage": 0.3,
        "maxCpuUsage": 1.87,
        "avgCpuUsage": 0.69,
        "day": "2024-05-22",
    }
]
```

### [응답 실패 예시]

- **HTTP Status**: 400 BadRequest
```json
{
    "code": "400",
    "message": "값이 존재하지 않습니다."
}
```
