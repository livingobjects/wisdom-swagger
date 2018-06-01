package com.livingobjects.wisdom.swagger.internal.bundledoc;

import org.osgi.framework.Bundle;

import java.util.Optional;

public interface BundleApiDoc {

    /**
     * Returns the only {@link SwaggerBundle}.
     * @return the only {@link SwaggerBundle} if one exists, empty otherwise.
     */
    Optional<SwaggerBundle> findSingle();

    /**
     * Finds a {@link SwaggerBundle} by a search key.
     * @param key the search key.
     * @return the {@link SwaggerBundle} if a bundle is found for the search key, empty otherwise.
     * @throws IllegalStateException if more than one bundle matches for the search key.
     */
    Optional<SwaggerBundle> findByKey(String key);

    /**
     * Adds a bundle with its swagger file to the documentation.
     * @param bundle the bundle to add to the documentation.
     * @param swaggerFile the swagger file path.
     */
    void addBundle(Bundle bundle, String swaggerFile);

    /**
     * Removes a bundle from the documentation.
     * @param bundle the bundle to remove from the documentation.
     */
    void removeBundle(Bundle bundle);
}
