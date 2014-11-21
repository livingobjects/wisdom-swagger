package com.livingobjects.wisdom.swagger.impls;

import com.google.common.collect.ImmutableSet;
import com.livingobjects.myrddin.ApiSpecification;
import org.osgi.framework.Bundle;

public final class BundleApiDoc {

    public final ImmutableSet<String> baseUris;

    public final String swaggerFile;

    public final ApiSpecification apiSpecification;

    public final Bundle bundle;

    public BundleApiDoc(ImmutableSet<String> baseUris, String swaggerFile, ApiSpecification apiSpecification, Bundle bundle) {
        this.baseUris = baseUris;
        this.swaggerFile = swaggerFile;
        this.apiSpecification = apiSpecification;
        this.bundle = bundle;
    }
}
