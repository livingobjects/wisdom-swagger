package com.livingobjects.wisdom.swagger.internal.bundledoc;

import com.livingobjects.wisdom.swagger.library.BundleApiDocLibrary;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class BundleApiDocServiceImplTest {

    private BundleApiDocServiceImpl bundleApiDocService;

    @Before
    public void setUp() {
        bundleApiDocService = new BundleApiDocServiceImpl();
    }

    @Test
    public void shouldNotFindDefaultBundle_whenThereIsNone() {
        Optional<BundleApiDoc> defaultBundleApiDoc = bundleApiDocService.findDefault();

        assertThat(defaultBundleApiDoc).isEmpty();
    }

    @Test
    public void shouldFindDefaultBundle_whenThereIsOnlyOneBundle() {
        BundleApiDoc bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        bundleApiDocService.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        Optional<BundleApiDoc> defaultBundleApiDoc = bundleApiDocService.findDefault();

        assertThat(defaultBundleApiDoc).isNotEmpty();
        assertThat(defaultBundleApiDoc.get().bundle.getSymbolicName()).isEqualTo(bundleApiDocTom.bundle.getSymbolicName());
    }

    @Test
    public void shouldNotFindDefaultBundle_whenNoneMatchesTheOneInTheConfig() {
        BundleApiDocConfig config = mock(BundleApiDocConfig.class);
        doReturn("noBundleIsNamedLikeThis").when(config).defaultBundleName();

        bundleApiDocService.activate(config);

        BundleApiDoc bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        BundleApiDoc bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        bundleApiDocService.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        bundleApiDocService.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        Optional<BundleApiDoc> defaultBundleApiDoc = bundleApiDocService.findDefault();

        assertThat(defaultBundleApiDoc).isEmpty();
    }

    @Test
    public void shouldFindDefaultBundle_whenOneMatchesTheOneInTheConfig() {
        BundleApiDocConfig config = mock(BundleApiDocConfig.class);
        doReturn("tomSearle").when(config).defaultBundleName();

        bundleApiDocService.activate(config);

        BundleApiDoc bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        BundleApiDoc bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        bundleApiDocService.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        bundleApiDocService.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        Optional<BundleApiDoc> defaultBundleApiDoc = bundleApiDocService.findDefault();

        assertThat(defaultBundleApiDoc).isNotEmpty();
        assertThat(defaultBundleApiDoc.get().bundle.getSymbolicName()).isEqualTo(bundleApiDocTom.bundle.getSymbolicName());
    }

    @Test
    public void shouldNotFindBundleByKey_whenOneNeverExisted() {
        String key = "tomSearle";

        Optional<BundleApiDoc> bundleApiDoc = bundleApiDocService.findByKey(key);

        assertThat(bundleApiDoc).isEmpty();
    }

    @Test
    public void shouldNotFindBundleByKey_whenOneDoesntExistAnymore() {
        String key = "tomSearle";

        BundleApiDoc bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        BundleApiDoc bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        bundleApiDocService.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        bundleApiDocService.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);
        bundleApiDocService.removeBundle(bundleApiDocTom.bundle);

        Optional<BundleApiDoc> bundleApiDoc = bundleApiDocService.findByKey(key);

        assertThat(bundleApiDoc).isEmpty();
    }

    @Test
    public void shouldFindBundleByKey_whenOneExists() {
        String key = "tomSearle";

        BundleApiDoc bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        BundleApiDoc bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        bundleApiDocService.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        bundleApiDocService.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        Optional<BundleApiDoc> bundleApiDoc = bundleApiDocService.findByKey(key);

        assertThat(bundleApiDoc).isNotEmpty();
        assertThat(bundleApiDoc.get().bundle.getSymbolicName()).isEqualTo(bundleApiDocTom.bundle.getSymbolicName());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowException_whenMoreThanOneBundleMatchesASearchKey() {
        String key = "searle";

        BundleApiDoc bundleApiDocDan = BundleApiDocLibrary.bundleApiDocDan();
        BundleApiDoc bundleApiDocTom = BundleApiDocLibrary.bundleApiDocTom();

        bundleApiDocService.addBundle(bundleApiDocDan.bundle, bundleApiDocDan.swaggerFile);
        bundleApiDocService.addBundle(bundleApiDocTom.bundle, bundleApiDocTom.swaggerFile);

        bundleApiDocService.findByKey(key);
    }
}