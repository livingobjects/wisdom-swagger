/*
 * #%L
 * Wisdom-Swagger
 * %%
 * Copyright (C) 2014 LivingObjects SAS
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.livingobjects.wisdom.swagger;

import com.google.common.collect.ImmutableSet;
import com.livingobjects.myrddin.ApiSpecification;
import com.livingobjects.myrddin.Wizard;
import com.livingobjects.myrddin.exception.SwaggerException;
import com.livingobjects.wisdom.swagger.impls.BundleApiDoc;
import org.apache.commons.io.IOUtils;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.extender.Extender;
import org.osgi.framework.Bundle;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.PathParameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.annotations.View;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.MimeTypes;
import org.wisdom.api.http.Result;
import org.wisdom.api.router.Router;
import org.wisdom.api.templates.Template;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@Extender(
        onArrival = "onBundleArrival",
        onDeparture = "onBundleDeparture",
        extension = "Swagger-Doc")
public final class SwaggerDocController extends DefaultController {

    private final ConcurrentHashMap<String, BundleApiDoc> baseUriMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ImmutableSet<String>> bundleBaseUris = new ConcurrentHashMap<>();
    @Requires
    protected Router router;
    @View("swagger-doc")
    protected Template swaggerDocView;
    @View("swagger-index")
    protected Template swaggerIndexView;

    @Route(method = HttpMethod.GET, uri = "/api-doc")
    public Result displayDocumentationIndex() {
        return ok(render(swaggerIndexView, "uris", baseUriMap.keySet()));
    }

    @Route(method = HttpMethod.GET, uri = "/api-doc/{api}")
    public Result displayDocumentation(@PathParameter("api") String api) {
        BundleApiDoc apiDoc = baseUriMap.get(api);
        if (apiDoc != null) {
            return ok(render(swaggerDocView, "apiBaseUri", api, "api", apiDoc.apiSpecification, "router", router));
        } else {
            return notFound();
        }
    }

    @Route(method = HttpMethod.GET, uri = "/api-doc/{api}/raw")
    public Result displayRawDocumentation(@PathParameter("api") String api) {
        BundleApiDoc apiDoc = baseUriMap.get(api);
        if (apiDoc != null) {
            URL url = apiDoc.bundle.getResource(apiDoc.swaggerFile);
            if (url != null) {
                try (InputStream in = url.openStream()) {
                    return ok(IOUtils.toString(in)).as(MimeTypes.TEXT);
                } catch (IOException e) {
                    logger().error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", apiDoc.swaggerFile, apiDoc.bundle.getSymbolicName(), e);
                    return notFound();
                }
            } else {
                logger().error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", apiDoc.swaggerFile, apiDoc.bundle.getSymbolicName());
                return notFound();
            }
        } else {
            return notFound();
        }
    }

    /**
     * Notified when a bundle is loaded with a given Swagger-Doc header in its manifest. It must contains the header to be loaded by SwaggerDocController.
     * Read the swagger file contained in the header. Serves all the base routes defined in the documentation to display the documentation.
     *
     * @param bundle      The new loaded bundle.
     * @param swaggerFile The swaggerFile path in the manifest.
     */
    void onBundleArrival(Bundle bundle, String swaggerFile) {
        try {
            try (InputStream in = bundle.getResource(swaggerFile).openStream()) {
                if (in != null) {
                    Wizard wizard = new Wizard();
                    try {
                        ApiSpecification apiSpecification = wizard.generateSpecification(in);
                        Set<String> baseUris = apiSpecification.resources.stream().map(r -> {
                            String[] uris = r.uri.split("/");
                            if (uris.length > 2) {
                                return uris[1];
                            } else {
                                return null;
                            }
                        }).filter(s -> s != null).collect(Collectors.toSet());
                        ImmutableSet<String> immutableBaseUris = ImmutableSet.copyOf(baseUris);
                        BundleApiDoc apiDoc = new BundleApiDoc(immutableBaseUris, swaggerFile, apiSpecification, bundle);
                        for (String baseUri : immutableBaseUris) {
                            baseUriMap.put(baseUri, apiDoc);
                        }
                        bundleBaseUris.put(bundle.getSymbolicName(), immutableBaseUris);
                    } catch (SwaggerException e) {
                        logger().error("Swagger documentation '{}' for bundle '{}' is invalid.", swaggerFile, bundle.getSymbolicName(), e);
                    }
                } else {
                    logger().error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", swaggerFile, bundle.getSymbolicName());
                }
            } catch (IOException e) {
                logger().error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", swaggerFile, bundle.getSymbolicName(), e);
            }
        } catch (IllegalArgumentException e) {
            logger().error("The Swagger-Doc attribute in manifest of bundle '{}' is invalid. It must be of the form 'uri:yaml-resource-path'.", bundle.getSymbolicName());
        }
    }

    /**
     * Notified when a given bundle is unloaded. Clear the corresponding uris stored in the maps.
     *
     * @param bundle The unloaded bundle.
     */
    void onBundleDeparture(Bundle bundle) {
        String name = bundle.getSymbolicName();
        Set<String> baseUris = bundleBaseUris.get(name);
        if (baseUris != null) {
            baseUris.forEach(baseUriMap::remove);
            bundleBaseUris.remove(name);
        }
    }
}
