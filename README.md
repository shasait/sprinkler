# sprinkler

[![Maven Central](https://img.shields.io/maven-central/v/de.hasait.sprinkler/sprinkler.svg?label=Maven%20Central)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.hasait.sprinkler%22%20AND%20a%3A%22sprinkler%22)

Application for controlling sprinklers using Raspberry Pi.

* Licensed under the Apache License, Version 2.0
* Web frontend to configure schedules
* Sprinkler runtime can be reduced by amount of rain
* Relay providers:
    * GPIO via `/sys/class/gpio` (e.g. relay boards for Raspberry Pi)
* Supported sensor providers, e.g. for determining rain:
    * [Hamburg Wasser](https://sri.hamburgwasser.de/)
* Extend by implementing interfaces to support more relays and sensors:
    * [`RelayProvider`](src/main/java/de/hasait/sprinkler/service/relay/provider/RelayProvider.java)
    * [`SensorProvider`](src/main/java/de/hasait/sprinkler/service/sensor/provider/SensorProvider.java)

## Installation

The following steps roughly summarize [Spring Boot Application as systemd Service](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.installing.nix-services.system-d).
For Windows take a look [here](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.installing.windows-services).

1) Create a service user `sprinkler`
2) Create a service folder `/srv/sprinkler` and ensure the service user can write into it
3) Create a log folder `/var/log/sprinkler` and ensure the service user can write into it
4) Download the latest JAR as `sprinkler.jar` into service folder
5) Create file `sprinkler.conf` in service folder:
    ```
    JAVA_OPTS=-Xmx512m
    LOG_FOLDER=/var/log/sprinkler
    ```
6) Create file `application.properties` in service folder and replace TODOs:
    ``` 
    # Nothing here currently
    ```
7) Create file `users.json` in service folder based on [users.json](users.json)

8) Create systemd unit `/etc/systemd/system/sprinkler.service`:
    ```
    [Unit]
    Description=sprinkler service
    After=network.target
    
    [Service]
    User=sprinkler
    Group=sprinkler
    ExecStart=/srv/sprinkler/sprinkler.jar
    SuccessExitStatus=143
    StandardOutput=file:/var/log/sprinkler/sprinkler.log
    StandardError=inherit
    
    [Install]
    WantedBy=multi-user.target
    ```

9) Start and enable service:
    * Start now: `systemctl start sprinkler.service`
    * Check status: `systemctl status sprinkler.service`
    * Enable at boot: `systemctl enable sprinkler.service`

## Development / Contribution

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/#build-image)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)
* [Vaadin](https://vaadin.com/docs)
* [Spring Security](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/#web.security)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Validation](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/#io.validation)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/#actuator)

### Guides

The following guides illustrate how to use some features concretely:

* [Creating CRUD UI with Vaadin](https://spring.io/guides/gs/crud-with-vaadin/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### TODO

* Graph for visualization of Sensor values
