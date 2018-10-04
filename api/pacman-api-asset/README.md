# PacMan - API - Assets
Any enterprise will have a number of assets under its IT infrastructure. These assets could be hosted on an on premises data center or could be on a private cloud or a public cloud like AWS. This microservice consolidates all such information about assets and presents it to the user in different perspectives. The Assets Dashboard and the Assets Listing page in the UI invokes this microservice to render all the required data. This microservice contains all such APIs related to assets. For e.g. getting a count of assets, listing attributes on assets, searching for assets etc. The assets could be onprem or could be inside a cloud.

## How to set it up?
The application can be started by running the Java class com.tmobile.pacman.api.asset.AssetApplication which is the Spring Boot starter class. 

This microservice should be connected to the Spring Cloud Config microservice. It will pull all the configurations from the Spring Cloud Config microservice.
The URL of the config microservice should be provided as a program argument while starting up the assets service. 
For e.g.:
CONFIG_SERVER_URL=https://dev-api.pacman.your-company.com/config
The context root for the service is /asset. The URL would be:
https://{host}/asset
