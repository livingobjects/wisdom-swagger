package com.livingobjects.wisdom.swagger.library;

import com.livingobjects.wisdom.swagger.internal.bundledoc.BundleApiDoc;
import org.osgi.framework.Bundle;

import java.net.URL;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class BundleApiDocLibrary {

    public static BundleApiDoc bundleApiDocDan() {
        String swaggerFile = SwaggerFileLibrary.swaggerFilePathDan();
        URL swaggerFileUrl = BundleApiDocLibrary.class.getResource(swaggerFile);

        Bundle bundleDanMock = mock(Bundle.class);
        doReturn("com.architects.dansearle").when(bundleDanMock).getSymbolicName();
        doReturn(swaggerFileUrl).when(bundleDanMock).getResource(swaggerFile);

        return new BundleApiDoc(bundleDanMock, swaggerFile);
    }

    public static BundleApiDoc bundleApiDocTom() {
        String swaggerFile = SwaggerFileLibrary.swaggerFilePathTom();
        URL swaggerFileUrl = BundleApiDocLibrary.class.getResource(swaggerFile);

        Bundle bundleTomMock = mock(Bundle.class);
        doReturn("com.architects.tomsearle").when(bundleTomMock).getSymbolicName();
        doReturn(swaggerFileUrl).when(bundleTomMock).getResource(swaggerFile);

        return new BundleApiDoc(bundleTomMock, swaggerFile);
    }
}
