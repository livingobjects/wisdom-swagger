wisdom-swagger
==============
[![Build Status](https://api.travis-ci.org/livingobjects/wisdom-swagger.png)](https://travis-ci.org/livingobjects/wisdom-swagger)

A swagger extension for wisdom framework which provides Swagger REST API documentation automatically, using the
[OpenApi 3 specification](https://github.com/OAI/OpenAPI-Specification)

## Why wisdom-swagger ?

This module is done to be deployed automatically to your wisdom application. It is fully OSGi compatible and dependencies are minimal.

This module is automatically notified by all the OSGi modules which declares a "Swagger-Doc" attribute in their MANIFEST.MF and provides the corresponding Swagger file in
[OpenApi 3 YAML format](https://github.com/OAI/OpenAPI-Specification)

When a bundle with a valid Swagger-Doc is found, wisdom-swagger will pick the OpenAPi file and serve:
- the [Swagger UI](https://github.com/swagger-api/swagger-ui)
- the raw [OpenApi 3](https://github.com/OAI/OpenAPI-Specification) files (in YAML)

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

You have to deploy the wisdom-swagger module and its dependencies : snakeyaml into your wisdom application.
The dependencies guava and slf4j-api are already included in wisdom base.
 
#### Create your swagger documentation

Into your own wisdom application, add a [OpenApi 3 YAML format](https://github.com/OAI/OpenAPI-Specification)
documentation file of your REST api, example:
```
src/main/resources/swagger/api.yaml
```

#### Declare your Swagger doc to wisdom-swagger

Into your wisdom application add Swagger-Doc attribute in the manifest pointing to the swagger file.
Example of MANIFEST.MF :

```
Private-Package: com.livingobjects.mywisdom
Swagger-Doc: swagger/api.yaml
```

Wisdom-swagger will be automatically notified at runtime by the manifest attribute and will server the documentation.

Example:

```
# Bundle1 : com.mybundles.bundle1
src/main/resources/swagger/api.yaml

# Bundle2 : com.mybundles.bundle2
src/main/resources/swagger/api.yaml
```

Lets assume wisdom is running on port 9000, then :

- The follow routes will be served :
*(application/x-yaml) http://localhost:9000/api-doc => will serve the default bundle OpenApi doc to display (see default bundle section below)
*(application/x-yaml)http://localhost:9000/api-doc/bundle1 => will serve the OpenApi doc defined in bundle1
*(application/x-yaml)http://localhost:9000/api-doc/bundle2 => will serve the OpenApi doc defined in bundle1

- Swagger UI will be displayed on http://localhost:9000/assets/doc/index.html
Swagger UI will try to request by default the /api-doc endpoint, but you can put /api-doc/bundle1 in the search bar and it will display
the OpenApi doc of the corresponding bundle.

#### The default bundle

If there is only one bundle then /api-doc will return the documentation of this bundle
If there is several bundles then /api-doc will return a 409 (HTTP CONFLICT), to avoid this, add a configuration file to your wisdom application, `wisdom-swagger.conf` such as:

```
components {
    bundle-api-doc-service {
        defaultBundleName = bundle2
    }
}
```