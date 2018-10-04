# PacMan - API - Statistics
This micro service is responsible for providing all the services for querying continuous compliance statistics Data. It provides all supporting APIs required to host the Statistics Dashboard

## How to set it up?
The application can be started by running the Java class com.tmobile.pacman.api.statistics.StatisticsApplication which is the Spring Boot starter class. 

This microservice should be connected to the Spring Cloud Config microservice. It will pull all the configurations from the Spring Cloud Config microservice.
The URL of the config microservice should be provided as a program argument while starting up the statistics service. 
For e.g.:
CONFIG_SERVER_URL=https://dev-api.pacman.your-company.com/config
The context root for the service is /statistics. The URL would be:
https://{host}/statistics