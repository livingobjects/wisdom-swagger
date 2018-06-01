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
package com.livingobjects.wisdom.swagger.internal.rest;

import com.google.common.annotations.VisibleForTesting;
import com.livingobjects.wisdom.swagger.internal.bundledoc.BundleApiDoc;
import com.livingobjects.wisdom.swagger.internal.bundledoc.BundleApiDocService;
import com.livingobjects.wisdom.swagger.internal.bundledoc.BundleUtils;
import org.apache.commons.io.IOUtils;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.extender.Extender;
import org.osgi.framework.Bundle;
import org.wisdom.api.DefaultController;
import org.wisdom.api.annotations.Controller;
import org.wisdom.api.annotations.PathParameter;
import org.wisdom.api.annotations.Route;
import org.wisdom.api.http.HttpMethod;
import org.wisdom.api.http.Result;
import org.wisdom.api.http.Results;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Extender(
    onArrival = "onBundleArrival",
    onDeparture = "onBundleDeparture",
    extension = "Swagger-Doc"
)
@Controller
public final class SwaggerDocController extends DefaultController {

    private static final String YAML_MIME_TYPE = "application/x-yaml";

    @Requires
    private BundleApiDocService bundleApiDocService;

    @Route(method = HttpMethod.GET, uri = "/api-doc")
    public Result apiDoc() {
        return bundleApiDocService.findDefault()
            .map(this::renderAsYaml)
            .orElse(notFound());
    }

    @Route(method = HttpMethod.GET, uri = "/api-doc/{key}")
    public Result apiDocByKey(@PathParameter("key") String key) {
        try {
            return bundleApiDocService.findByKey(key)
                .map(this::renderAsYaml)
                .orElse(notFound());
        } catch (IllegalStateException e) {
            return Results.status(CONFLICT);
        }
    }

    private Result renderAsYaml(BundleApiDoc bundleApiDoc) {
        String bundleName = BundleUtils.nameOf(bundleApiDoc.bundle);

        URL url = bundleApiDoc.bundle.getResource(bundleApiDoc.swaggerFile);

        if (url != null) {
            try (InputStream in = url.openStream()) {
                return ok(IOUtils.toString(in)).as(YAML_MIME_TYPE);
            } catch (IOException e) {
                logger().error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", bundleApiDoc.swaggerFile, bundleName, e);
                return notFound();
            }
        } else {
            logger().error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", bundleApiDoc.swaggerFile, bundleName);
            return notFound();
        }
    }

    /**
     * Notified when a bundle is loaded with a given Swagger-Doc header in its manifest. It must contains the header to be loaded.
     * @param bundle The new loaded bundle.
     * @param swaggerFile The swaggerFile path in the manifest.
     */
    void onBundleArrival(Bundle bundle, String swaggerFile) {
        bundleApiDocService.addBundle(bundle, swaggerFile);
    }

    /**
     * Notified when a given bundle is unloaded.
     * @param bundle The unloaded bundle.
     */
    void onBundleDeparture(Bundle bundle) {
        bundleApiDocService.removeBundle(bundle);
    }

    @VisibleForTesting
    void setBundleApiDocService(BundleApiDocService bundleApiDocService) {
        this.bundleApiDocService = bundleApiDocService;
    }
}
