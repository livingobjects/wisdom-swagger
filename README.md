wisdom-swagger
==============
[![Build Status](https://api.travis-ci.org/livingobjects/wisdom-swagger.png)](https://travis-ci.org/livingobjects/wisdom-swagger)

A swagger extension for wisdom framework which provides Swagger REST API documentation automatically (<a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md">Swagger spec v2.0</a>).
For information about supported format see parser : https://github.com/livingobjects/myrddin/blob/develop/README.md

## Why wisdom-swagger ?

This module is done to be deployed automatically to your wisdom application. It is fully OSGi compatible and dependencies are minimal (myrddin, snakeyaml).

This module is automatically notified by all the OSGi modules which declares a "Swagger-Doc" attribute in their MANIFEST.MF and provides the corresponding Swagger file in <a href="https://github.com/swagger-api/swagger-spec/blob/master/versions/2.0.md">YAML Swagger 2.0 format</a>.

When a bundle with a valid Swagger-Doc is found, wisdom-swagger will parse automatically the Swagger documentation and serve:
- an HTML page which display the REST API documentation as a styled UI.
- and the raw swagger file (in YAML)

See details below for the routes created by wisdom-swagger.

## Build

Run maven

```shell
mvn clean install
```

To deploy to your local repository use "altDeploymentRepository" option like this :

```shell
mvn deploy -DaltDeploymentRepository=nexus::default::http://xxx.xx.xx.xx:8081/nexus/content/repositories/snapshots
```

Or to deploy a release:

```shell
mvn deploy -DaltDeploymentRepository=nexus::default::http://xxx.xx.xx.xx:8081/nexus/content/repositories/releases
```

## How to use

#### Add wisdom-swagger dependency

You have to deploy the wisdom-swagger module and its dependencies : myrddin, snakeyaml into your wisdom application. The dependencies guava and slf4j-api are already included in wisdom base.
 
#### Create your swagger documentation

Into your own wisdom application, add a Swagger YAML documentation of your REST api, example:
```
src/main/resources/swagger/api.yaml
```

See example on Swagger 2.0 format on http://editor.swagger.io/

#### Declare your Swagger doc to wisdom-swagger

Into your wisdom application add Swagger-Doc attribute in the manifest pointing to the swagger file.
Example of MANIFEST.MF :

```
Private-Package: com.livingobjects.mywisdom
Swagger-Doc: swagger/api.yaml
```

Wisdom-swagger will be automatically notified at runtime by the manifest attrbiute and will server the documentation.

It will read all the base uris routes defined into your Swagger documentation and serve a page for these uris.

Example:

```yaml
#Swagger file
paths:
  /route1/test
  [...]
  /route2/test
  [...]
```

The following urls will be served:

 http://localhost:9000/api-doc/route1

 http://localhost:9000/api-doc/route2

 http://localhost:9000/api-doc/route1/raw

 http://localhost:9000/api-doc/route2/raw


