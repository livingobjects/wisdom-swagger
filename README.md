wisdom-swagger
==============

A swagger extension for wisdom framework which provides Swagger REST API documentation automatically.

## OSGI

Wisdom-swagger module is a fully osgi compatible bundle.

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


