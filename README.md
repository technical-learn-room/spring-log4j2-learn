# 스프링에서 Log4j2로 로그를 잘 찍어보자
![Log4j2 Logo](https://user-images.githubusercontent.com/48639421/127439750-9ec533c9-dd2e-4f6e-8817-69d2ec18ad52.png)  

---

## Log4j2가 뭐야?
`Log4j2`는 유명한 로깅 라이브러리로 스프링에서는 자체적으로 `Logback`이라는 로깅 라이브러리를 사용하고 있습니다.  
`Log4j2`는 `Logback`에 비해 어느 상황에서나 더 빠른 속도를 가지고 있으며  
자바의 람다식을 활용할 수 있는 메소드도 정의되어 있습니다.  

---

## 로그의 수준
로그를 찍기 전에 로그의 수준에 대해서는 확실히 알고 있어야 한다고 생각합니다.  
로그의 수준은 `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`, `FATAL`이 존재하며  
수준은 `TRACE`가 가장 낮은 수준의 로그로, 가장 위험도가 낮은 평범한 로그입니다.  
따라서 로그의 수준이 `INFO`라면 `INFO` 수준을 포함하여 그보다 위험한 로그들만 찍히게 됩니다.  

이외에도 모든 로그를 찍겠다는 `ALL` 수준과 모든 로그를 찍지 않겠다는 `OFF` 수준이 있으나,  
이는 개념상으로 있는 것이고 `ALL`은 가장 낮은 로그 수준인 `TRACE`와 같고  
`OFF`는 로그를 사용하지 않는 것과 같습니다.  

---

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

---

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
        <Console name="console">
            <PatternLayout pattern="${logPattern}"/>
        </Console>
        <RollingFile
                name="file"
                append="true"
                fileName="${logPath}/${serviceName}.log"
                filePattern="${logPath}/${serviceName}.%d{yyyy-MM-dd}.%i.log.gz">
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

---

## log4j2.xml 설정을 파헤쳐보자

1. **Configuration::status**
   > 정확한 정보는 [이 글](https://stackoverflow.com/questions/21065854/what-does-status-mean-in-log4j2-configuration)을 참조해주세요!

2. **Properties.Property**
   `Properties.Property`는 설정 파일 안에서 사용할 변수를 정의하는 태그입니다.  
   `Gradle` 파일에서 버전을 한 곳에서 관리하는 것과 같다고 생각하시면 됩니다.  

   위 설정파일에서는 로그를 저장할 디렉토리 (logPath), 작성될 로그의 패턴 (logPattern),  
   서비스 이름 (로그 파일의 이름으로 사용함, serviceName)을 프로퍼티로 저장하였습니다.  

   이렇게 저장한 프로퍼티를 사용할 때는 `${property-name}` 형태로 사용하면 됩니다.

3. **Appenders.Console**
   여기서는 콘솔에 찍히는 로그에 대해 정의하는 곳입니다.  
   나중에 이 콘솔에 로그를 남길 로거를 결정하는데 필요한 `name` 속성을 지정하고,  
   콘솔에 찍힐 로그의 패턴을 정의합니다.  

4. **Appenders.RollingFile**
   여기서는 파일에 저장되는 로그에 대해 정의하는 곳입니다.  
   콘솔과 마찬가지로 로그를 남길 로거를 결정하는데 필요한 `name` 속성의 지정하고,  
   `append` 속성을 활성화함으로서 로그를 파일에 쓸 때 계속 파일의 첫 부분에 덮어쓰는 문제를 해결할 수 있습니다.  
   `fileName` 속성은 생성되는 로그를 저장할 로그 파일의 이름입니다.  
   `filePattern` 속성은 `fileName`에 지정한 로그 파일이 어떠한 이유로 분리될 경우 생성할 로그 파일의 패턴입니다.  

   > append 속성에 대한 문제는 [이 글](https://stackoverflow.com/questions/54592406/log4j2-rollingfile-appender)에서 확인하실 수 있습니다.  

5. **Appenders.RollingFile.PatternLayout**
   로그 파일에 저장될 로그의 형식을 정의합니다.    

6. **Appenders.RollingFile.Policies**
   이곳이 `Rolling File`의 핵심적인 규칙을 정의하는 곳입니다.  
   여기서는 여러 `Policy(정책)`을 정의할 수 있습니다.  
   본문에서 정의한 정책은 두 가지입니다.  

   6-1. **SizeBasedTriggeringPolicy**   
   
        이 정책은 로그 파일의 크기에 따라 로그 파일을 나누는 기준을 정의합니다.  
        본문에서는 `size` 속성을 `5MB`로 정의하면서 로그 파일이 5MB가 되면 이를 따로 떼어내어  
        새로운 로그 파일로 만들게 됩니다.  
   6-2. **TimeBasedTriggeringPolicy**  
   
        이 정책은 로그 파일이 시간에 따라 로그 파일을 나누는 기준을 정의합니다.  
        위의 `filePattern`에는 `${logPath}/${serviceName}.%d{yyyy-MM-dd}.%i.log.gz`이라고 정의되어 있는데  
        파일 이름에 날짜를 지정하여 날짜별로 로그 파일이 생기게 만들었습니다.  
        이로인해 로그 파일의 크기가 `5MB`가 되지 않더라도 하루가 지나게 되면 새로운 로그 파일이 생성됩니다.  
        그리고 `%i`는 하루 안에 로그 파일이 `5MB`가 넘어버리면 똑같은 이름의 로그 파일이 두 개 생길 수도 있으므로  
        파일 카운팅을 하여 1부터 순차대로 올라가는 변수를 삽입해줍니다.  
        이는 매일마다 초기화됩니다.  
   
   로그 파일의 크기에 대한 정책은 로그가 일정 크기까지 쌓이지 않으면 메인 로그 파일에만 계속 쌓이게 되며  
   시간에 대한 정책은 가장 작은 단위가 끝났을 때 로그 파일이 쌓이게 됩니다.  
   예를 들면 `yyyy-MM-dd-HH.log` 라는 `filePattern`을 지정해두었다면  
   `2021-07-29-16.log` 파일은 오후 5시가 되었을 때 생성된다는 뜻입니다.  
   물론 로그 파일의 크기가 일정량을 넘어가면 강제로 생성되기도 합니다.  

   이렇게 어떠한 정책에 의해 로그 파일을 계속해서 나누어 관리하는 기법을 `Rolling File` 기법이라고 합니다.  

7. **Loggers.Logger**
   `name` 속성을 통해 어떤 패키지에서 나오는 로그를 처리할 건지 설정할 수 있고,  
   `level` 속성을 통해 어떤 로그 수준부터 로깅할 것인지를 설정할 수 있으며,  
   `additivity` 속성을 비활성화하여 다른 로거와 로깅하는 일을 겹치지 않게 할 수 있습니다.  
   
   여기서 `level` 속성은 아래의 `Loggers.Logger.AppenderRef::level`에 대신 정의하여  
   콘솔과 파일에 하는 로깅 수준을 다르게 정의할 수도 있습니다.  
