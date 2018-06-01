package com.livingobjects.wisdom.swagger.internal.bundledoc;

import com.livingobjects.wisdom.swagger.library.BundleApiDocLibrary;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public final class InMemoryBundleApiDocTest {

    private InMemoryBundleApiDoc inMemoryBundleApiDoc;

    @Before
    public void setUp() {
        inMemoryBundleApiDoc = new InMemoryBundleApiDoc();
    }

    @Test
    public void shouldNotFindDefaultBundle_whenThereIsNone() {
        Optional<SwaggerBundle> defaultBundleApiDoc = inMemoryBundleApiDoc.findSingle();

        assertThat(defaultBundleApiDoc).isEmpty();
    }

    @Test
    public void shouldNotFindSingleBundle_whenThereIsNone() {
        Optional<SwaggerBundle> singleBundleApiDoc = inMemoryBundleApiDoc.findSingle();

        assertThat(singleBundleApiDoc).isEmpty();
    }

    @Test
    public void shouldNotFindSingleBundle_whenThereIsMoreThanOne() {
        SwaggerBundle bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        SwaggerBundle bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        inMemoryBundleApiDoc.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        inMemoryBundleApiDoc.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        Optional<SwaggerBundle> singleBundleApiDoc = inMemoryBundleApiDoc.findSingle();

        assertThat(singleBundleApiDoc).isEmpty();
    }

    @Test
    public void shouldFindSingleBundle_whenThereIsOnlyOne() {
        SwaggerBundle bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        inMemoryBundleApiDoc.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        Optional<SwaggerBundle> singleBundle = inMemoryBundleApiDoc.findSingle();

        assertThat(singleBundle).isNotEmpty();
        assertThat(singleBundle.get().bundle.getSymbolicName()).isEqualTo(bundleApiDocTom.bundle.getSymbolicName());
    }

    @Test
    public void shouldNotFindBundleByKey_whenOneNeverExisted() {
        String key = "tomSearle";

        Optional<SwaggerBundle> bundleApiDoc = inMemoryBundleApiDoc.findByKey(key);

        assertThat(bundleApiDoc).isEmpty();
    }

    @Test
    public void shouldNotFindBundleByKey_whenOneDoesntExistAnymore() {
        String key = "tomSearle";

        SwaggerBundle bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        SwaggerBundle bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        inMemoryBundleApiDoc.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        inMemoryBundleApiDoc.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);
        inMemoryBundleApiDoc.removeBundle(bundleApiDocTom.bundle);

        Optional<SwaggerBundle> bundleApiDoc = inMemoryBundleApiDoc.findByKey(key);

        assertThat(bundleApiDoc).isEmpty();
    }

    @Test
    public void shouldFindBundleByKey_whenOneExists() {
        String key = "tomSearle";

        SwaggerBundle bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        SwaggerBundle bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        inMemoryBundleApiDoc.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        inMemoryBundleApiDoc.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        Optional<SwaggerBundle> bundleApiDoc = inMemoryBundleApiDoc.findByKey(key);

        assertThat(bundleApiDoc).isNotEmpty();
        assertThat(bundleApiDoc.get().bundle.getSymbolicName()).isEqualTo(bundleApiDocTom.bundle.getSymbolicName());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowException_whenMoreThanOneBundleMatchesASearchKey() {
        String key = "searle";

        SwaggerBundle bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        SwaggerBundle bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        inMemoryBundleApiDoc.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        inMemoryBundleApiDoc.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        inMemoryBundleApiDoc.findByKey(key);
    }
}