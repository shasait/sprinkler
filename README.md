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
