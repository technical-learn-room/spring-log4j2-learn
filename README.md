# 스프링에서 Log4j2로 로그를 잘 찍어보자
![Log4j2 Logo](https://user-images.githubusercontent.com/48639421/127439750-9ec533c9-dd2e-4f6e-8817-69d2ec18ad52.png)  

## Log4j2가 뭐야?
`Log4j2`는 유명한 로깅 라이브러리로 스프링에서는 자체적으로 `Logback`이라는 로깅 라이브러리를 사용하고 있습니다.  


## Log4j2를 사용해보자
### 의존성 추가
```build.gradle
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
}
```

### 로그 설정 파일 추가
로그 설정 파일은 기존 스프링 기본 설정 파일인 `application.yml` 또는 `application.properties`에서도 설정할 수 있고,  
`XML` 파일을 이용해서 설정을 구성할 수도 있습니다.  
대신 `XML` 파일을 따로 둘 경우 `XML` 파일의 `classpath`를 지정해주어야 합니다.

```yml
logging:
  config: classpath:log4j2.xml
```

### 스프링 Logback 제거하기
스프링에서는 기본적으로 `Logback`을 이용해서 로깅을 하기 때문에  
다른 로깅 라이브러리인 `Log4j2`를 그냥 도입하게 되면 로깅 라이브러리끼리 충돌이 발생하기 때문에  
`Log4j2`를 적용하기 위해서는 `Logback` 라이브러리를 제거해야합니다.

```build.gradle
configurations {
    all {
        exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
    }
}
```

## log4j2.xml을 구성해보자
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="DEBUG">
    <Properties>
        <Property name="logPath">./logs</Property>
        <Property name="logPattern">[%-5level] %d{yyyy-MM-dd HH:mm:ss} [%t] %c{1} - %msg%n</Property>
        <Property name="serviceName">application</Property>
    </Properties>
    <Appenders>
        <Console name="console" direct="true" target="SYSTEM_OUT">
            <PatternLayout pattern="${logPattern}"/>
        </Console>
        <RollingFile
                name="file"
                append="true"
                fileName="${logPath}/${serviceName}.log"
                filePattern="${logPath}/${serviceName}.%d{yyyy-MM-dd}.%i.log.gz"
                ignoreExceptions="false">
            <PatternLayout pattern="${logPattern}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="5MB"/>
                <TimeBasedTriggeringPolicy/>
            </Policies>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Logger name="com.springframework" level="info" additivity="false">
            <AppenderRef ref="console"/>
            <AppenderRef ref="file"/>
        </Logger>
        <Logger name="com.j" level="info" additivity="false">
            <AppenderRef ref="console"/>
            <AppenderRef ref="file"/>
        </Logger>
    </Loggers>
</Configuration>
```
위 `log4j2.xml` 파일에서는 다음과 같은 로그 설정을 구성하고 있습니다.  
- ./logs/application.log 파일을 만들어 로그를 저장합니다.
- application.log 파일이 5MB가 되면 application.log 파일에 있는 로그를  
  ./logs/application.년도-월-일.파일숫자.log 파일로 옮기고 .gz 파일로 압축합니다.  
- com.springframework으로 시작하는 패키지에 존재하는 클래스들에서 작성된 로그는 info 수준 이상의 로그만 가져옵니다.
- 마찬가지로 com.j로 시작하는 패키지에 존재하는 클래스들에서 작성된 로그도 info 수준 이상의 로그만 가져옵니다.  

## log4j2.xml 설정을 파헤쳐보자

### Configuration::status

> 정확한 정보는 [이 글](https://stackoverflow.com/questions/21065854/what-does-status-mean-in-log4j2-configuration)을 참조해주세요!

### Properties.Property
`Properties.Property`는 설정 파일 안에서 사용할 변수를 정의하는 태그입니다.  
`Gradle` 파일에서 버전을 한 곳에서 관리하는 것과 같다고 생각하시면 됩니다.  
위 설정파일에서는 로그를 저장할 디렉토리 (logPath), 작성될 로그의 패턴 (logPattern),  
서비스 이름 (로그 파일의 이름으로 사용함, serviceName)을 프로퍼티로 저장하였습니다.  

### Appenders.Console
