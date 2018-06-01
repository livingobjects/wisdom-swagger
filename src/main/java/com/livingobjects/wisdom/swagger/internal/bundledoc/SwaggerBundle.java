package com.livingobjects.wisdom.swagger.internal.bundledoc;

import com.google.common.base.MoreObjects;
import org.osgi.framework.Bundle;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class SwaggerBundle {

    public final Bundle bundle;

    public final String swaggerFile;

    public SwaggerBundle(Bundle bundle, String swaggerFile) {
        this.swaggerFile = requireNonNull(swaggerFile);
        this.bundle = requireNonNull(bundle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwaggerBundle that = (SwaggerBundle) o;
        return Objects.equals(bundle, that.bundle) &&
            Objects.equals(swaggerFile, that.swaggerFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bundle, swaggerFile);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("bundle", bundle)
            .add("swaggerFile", swaggerFile)
            .toString();
    }
}
