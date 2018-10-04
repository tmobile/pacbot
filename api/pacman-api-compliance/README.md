# PacBot - API - Compliance
Compliance Service is Spring boot application and it calculates compliance of your AWS assets by each resource type. Compliance percentage is derived from assets and policy metrics(based on number of assets passed/failed for given policy)

PacBot Compliance reporting has below categories.

* Security

* Cost Optimization

* Governance

* Tagging

Compliance Service gives summary about all the policies and their compliance report.

Compliance Summary is linked with Asset List to see the list of compliant/non-complaint assets by every policy.


Compliance Service is Spring Boot Application and it has Controllers/Services/Repositories like any other Spring boot application.

All Repositories classes has logic related to fetch data from Elastic Search using queries.

All Services classes has logic related data processing by based on API response.

All Controller classes has Request mapping for all the micro services.

## How to Start Compliance Service?
To start compliance service, [Config](https://github.com/tmobile/pacman/wiki/Micro-Services/_edit#config-service) service pre-requisite.

Steps to [start](https://github.com/tmobile/pacman/wiki/Micro-Services#how-to-start-service) compliance service.
