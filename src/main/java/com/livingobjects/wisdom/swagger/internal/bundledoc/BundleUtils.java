package com.livingobjects.wisdom.swagger.internal.bundledoc;

import org.osgi.framework.Bundle;

public final class BundleUtils {

    public static String nameOf(Bundle bundle) {
        return bundle.getSymbolicName();
    }
}
