package com.livingobjects.wisdom.swagger.internal.bundledoc;

import org.osgi.framework.Bundle;

import java.util.Optional;

public interface BundleApiDocService {

    /**
     * Returns the default {@link BundleApiDoc}.
     * @return the default {@link BundleApiDoc} if one exists, empty otherwise.
     */
    Optional<BundleApiDoc> findDefault();

    /**
     * Finds a {@link BundleApiDoc} by a search key.
     * @param key the search key.
     * @return the {@link BundleApiDoc} if a bundle is found for the search key, empty otherwise.
     * @throws IllegalStateException if more than one bundle matches for the search key.
     */
    Optional<BundleApiDoc> findByKey(String key);

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
