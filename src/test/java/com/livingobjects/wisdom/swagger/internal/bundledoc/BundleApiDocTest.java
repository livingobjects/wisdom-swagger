package com.livingobjects.wisdom.swagger.internal.bundledoc;

import com.livingobjects.wisdom.swagger.library.SwaggerFileLibrary;
import org.junit.Test;
import org.osgi.framework.Bundle;

import static org.mockito.Mockito.mock;

public final class BundleApiDocTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowException_whenBundleIsNull() {
        new SwaggerBundle(null, SwaggerFileLibrary.swaggerFilePathTom());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowException_whenSwaggerFileIsNull() {
        Bundle bundleMock = mock(Bundle.class);
        new SwaggerBundle(bundleMock, null);
    }
}