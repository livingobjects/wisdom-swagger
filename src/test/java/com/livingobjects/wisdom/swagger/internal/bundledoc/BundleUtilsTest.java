package com.livingobjects.wisdom.swagger.internal.bundledoc;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

public final class BundleUtilsTest {

    @Test
    public void nameOfBundleShouldBeTheBundleSymbolicName() {
        String symbolicName = "com.symbolic.name";
        Bundle bundleMock = Mockito.mock(Bundle.class);
        doReturn(symbolicName).when(bundleMock).getSymbolicName();

        String bundleName  = BundleUtils.nameOf(bundleMock);

        assertThat(bundleName).isEqualTo(symbolicName);
    }
}