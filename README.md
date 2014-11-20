wisdom-swagger
==============

A swagger extension for wisdom framework which provides Swagger REST API documentation automatically.

## OSGI

Wisdom-swagger module is a fully osgi compatible bundle.

## How to use

1- You have to deploy the wisdom-swagger module and its dependencies : myrddin, snakeyaml into your wisdom application. The dependencies guava and slf4j-api are already included in wisdom base.
2- Into your own wisdom application, add a Swagger YAML documentation of your REST api, example:
src/main/resources/swagger/api.yaml
3- Into your wisdom application add Swagger-Doc attribute in the manifest pointing to the swagger file. Example:
MANIFEST.MF

Private-Package: com.livingobjects.cosmos.rest
Swagger-Doc: swagger/api.yaml


