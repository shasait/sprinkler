# sprinkler
Spring Boot application for controlling sprinklers using Raspberry Pi.

* Web frontend (Vaadin) to configure schedules
* Sprinkler runtime can be reduced by rain
* GPIO relay provider
* Hamburg Wasser rain service 
* Simply implement interfaces to support more relays and rain services:
  * `RelayProvider`
  * `RainService`
