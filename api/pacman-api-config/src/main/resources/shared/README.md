# PacMan - Config
This is a configuration repository. This repository will be read by the Spring Cloud Config microservice. The Cloud Config will feed these configurations to all microservices. These configurations can be changed on the fly. As soon as any commit is done to this repository, the changes can be propagated to any individual microservice without any restart, by invoking the http://{contextRoot}/refresh endpoint on the microservice(using HTTP POST method). All beans in all microservice have been given a spring scope of 'refresh', in order to enable this feature.  

## How to set it up?
There should be one branch for each environment. For e.g., if you have a dev, stage and a prod environment, you should have 3 branches, one for each environment. The URL for this repository should be provided to the Spring Cloud Config microservice so that it reads and keeps monitoring the repository for any commits.
