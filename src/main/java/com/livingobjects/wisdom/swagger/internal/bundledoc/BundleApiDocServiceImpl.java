package com.livingobjects.wisdom.swagger.internal.bundledoc;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toSet;

@Component(name = BundleApiDocServiceImpl.NAME, immediate = true)
public final class BundleApiDocServiceImpl implements BundleApiDocService {

    public static final String NAME = "bundle-api-doc-service";

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleApiDocServiceImpl.class);

    private final ConcurrentHashMap<String, BundleApiDoc> bundleApiDocByName = new ConcurrentHashMap<>();

    private String defaultBundleName = null;

    @Activate
    public void activate(BundleApiDocConfig config) {
        defaultBundleName = config.defaultBundleName();

        if (!Strings.isNullOrEmpty(defaultBundleName)) {
            LOGGER.info("The default bundle for api doc is {}", defaultBundleName);
        }
    }

    @Override
    public Optional<BundleApiDoc> findDefault() {
        if (bundleApiDocByName.isEmpty()) {
            return Optional.empty();
        } else if (bundleApiDocByName.size() == 1) {
            String onlyBundleName = Iterables.getOnlyElement(bundleApiDocByName.keySet());
            return Optional.of(bundleApiDocByName.get(onlyBundleName));
        }

        if (!Strings.isNullOrEmpty(defaultBundleName)) {
            return findByKey(defaultBundleName);
        }

        return Optional.empty();
    }

    @Override
    public Optional<BundleApiDoc> findByKey(String key) {
        Optional<String> bundleName = findBundleNameByKey(key);

        if (!bundleName.isPresent() || !bundleApiDocByName.containsKey(bundleName.get())) {
            return Optional.empty();
        }

        return Optional.of(bundleApiDocByName.get(bundleName.get()));
    }

    private Optional<String> findBundleNameByKey(String key) {
        Set<String> matchingBundleNames = bundleApiDocByName.keySet().stream()
            .filter(bundleName -> bundleName.toLowerCase().contains(key.toLowerCase()))
            .collect(toSet());

        if (matchingBundleNames.isEmpty()) {
            return Optional.empty();
        } else  if (matchingBundleNames.size() == 1) {
            return Optional.of(Iterables.getOnlyElement(matchingBundleNames));
        } else {
            throw new IllegalStateException(String.format("More than one bundle match for the key %s", key));
        }
    }

    @Override
    public void addBundle(Bundle bundle, String swaggerFile) {
        String bundleName = BundleUtils.nameOf(bundle);

        try {
            try (InputStream in = bundle.getResource(swaggerFile).openStream()) {
                if (in != null) {
                    BundleApiDoc apiDoc = new BundleApiDoc(bundle, swaggerFile);
                    bundleApiDocByName.put(bundleName, apiDoc);

                    LOGGER.info("Added Swagger documentation of bundle '{}'", bundleName);
                } else {
                    LOGGER.error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", swaggerFile, bundleName);
                }
            } catch (IOException e) {
                LOGGER.error("Swagger documentation '{}' not found for bundle '{}'. Check Swagger-Doc attribute in manifest.", swaggerFile, bundleName, e);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("The Swagger-Doc attribute in manifest of bundle '{}' is invalid. It must be of the form 'uri:yaml-resource-path'.", bundleName);
        }
    }

    @Override
    public void removeBundle(Bundle bundle) {
        String bundleName = BundleUtils.nameOf(bundle);
        bundleApiDocByName.remove(bundleName);
        LOGGER.info("Removed Swagger documentation of bundle '{}'", bundleName);
    }
}
