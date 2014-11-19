package com.livingobjects.wisdom.swagger.impls;

import com.livingobjects.myrddin.ApiSpecification;
import org.osgi.framework.Bundle;

import java.util.Set;

public final class BundleApiDoc {

    public final Set<String> baseUris;

    public final String swaggerFile;

    public final ApiSpecification apiSpecification;

    public final Bundle bundle;

    public BundleApiDoc(Set<String> baseUris, String swaggerFile, ApiSpecification apiSpecification, Bundle bundle) {
        this.baseUris = baseUris;
        this.swaggerFile = swaggerFile;
        this.apiSpecification = apiSpecification;
        this.bundle = bundle;
    }
}
