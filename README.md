# sprinkler

[![Maven Central](https://img.shields.io/maven-central/v/de.hasait.sprinkler/sprinkler.svg?label=Maven%20Central)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.hasait.sprinkler%22%20AND%20a%3A%22sprinkler%22)

Application for controlling sprinklers using Raspberry Pi.

* Licensed under the Apache License, Version 2.0
* Web frontend to configure schedules
* Sprinkler runtime can be reduced by rain
* Relay providers
  * GPIO via `/sys/class/gpio`
* Supported rain services
  * [Hamburg Wasser](https://sri.hamburgwasser.de/)
* Simply implement interfaces to support more relays and rain services:
  * [`RelayProvider`](src/main/java/de/hasait/sprinkler/service/relay/RelayProvider.java)
  * [`RainService`](src/main/java/de/hasait/sprinkler/service/weather/RainService.java)

## Installation

The following steps roughly summarize [Spring Boot Application as systemd Service](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.installing.nix-services.system-d).
For Windows take a look [here](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.installing.windows-services).

1) Create a service user `sprinkler`
2) Create a service folder `/srv/sprinkler` and ensure the service user can write into it
3) Create a log folder `/var/log/sprinkler` and ensure the service user can write into it
4) Download the latest JAR as `sprinkler.jar` into service folder
5) Create file `sprinkler.conf`:
    ```
    JAVA_OPTS=-Xmx512m
    LOG_FOLDER=/var/log/sprinkler
    ```
6) Create file `application.properties` (you need to replace the TODOs):
    ```
    # TODO Please adapt to your hardware relays 
    relay.gpio.relayToGpio: Relay1=sysclass@4,Relay2=sysclass@22,Relay3=sysclass@6,Relay4=sysclass@26
   
    rain.hww.sriLayer=22
    rain.hww.spatialReference=TODO
    rain.hww.positionX=TODO
    rain.hww.positionY=TODO
    ```
7) Create systemd unit `/etc/systemd/system/sprinkler.service`:
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

8) Enable service by executing: `systemctl enable sprinkler.service`
