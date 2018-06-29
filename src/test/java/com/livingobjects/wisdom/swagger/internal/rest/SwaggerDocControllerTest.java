package com.livingobjects.wisdom.swagger.internal.rest;

import com.livingobjects.wisdom.swagger.internal.bundledoc.BundleApiDoc;
import com.livingobjects.wisdom.swagger.internal.bundledoc.SwaggerBundle;
import com.livingobjects.wisdom.swagger.internal.bundledoc.SwaggerDocConfigService;
import com.livingobjects.wisdom.swagger.library.BundleApiDocLibrary;
import org.junit.Before;
import org.junit.Test;
import org.wisdom.api.http.Result;
import org.wisdom.test.parents.Action;
import org.wisdom.test.parents.Invocation;
import org.wisdom.test.parents.WisdomUnitTest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class SwaggerDocControllerTest extends WisdomUnitTest {

    private BundleApiDoc bundleApiDocsMock;
    private SwaggerDocConfigService bundleApiDocConfigServiceMock;
    private SwaggerDocController swaggerDocController;

    @Before
    public void setUp() {
        bundleApiDocsMock = mock(BundleApiDoc.class);
        bundleApiDocConfigServiceMock = mock(SwaggerDocConfigService.class);

        swaggerDocController = new SwaggerDocController();
        swaggerDocController.setBundleApiDoc(bundleApiDocsMock);
        swaggerDocController.setBundleApiDocConfigService(bundleApiDocConfigServiceMock);
    }

    @Test
    public void shouldReturnHttpNotFound_whenNoDefaultBundleNameAndNoApiDocExists() {
        doReturn(null).when(bundleApiDocConfigServiceMock).defaultBundleName();

        doReturn(Optional.empty()).when(bundleApiDocsMock).findSingle();

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDoc();
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        verify(bundleApiDocsMock).findSingle();
    }

    @Test
    public void shouldReturnHttpNotFound_whenDefaultBundleNameAndNoApiDocExists() {
        doReturn("default").when(bundleApiDocConfigServiceMock).defaultBundleName();

        doReturn(Optional.empty()).when(bundleApiDocsMock).findByKey("default");

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDoc();
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        verify(bundleApiDocsMock).findByKey("default");
    }

    @Test
    public void shouldReturnHttpOk_whenNoDefaultBundleNameAndApiDocExists() throws Exception {
        doReturn(null).when(bundleApiDocConfigServiceMock).defaultBundleName();
        SwaggerBundle bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();

        String expectedApiDoc = new String(Files.readAllBytes(Paths.get(getClass().getResource(bundleApiDoc.swaggerFile).toURI())));

        doReturn(Optional.of(bundleApiDoc)).when(bundleApiDocsMock).findSingle();

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDoc();
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/x-yaml");
        assertThat(toString(result)).isEqualTo(expectedApiDoc);

        verify(bundleApiDocsMock).findSingle();
    }

    @Test
    public void shouldReturnHttpOk_whenDefaultBundleNameAndApiDocExists() throws Exception {
        doReturn("default").when(bundleApiDocConfigServiceMock).defaultBundleName();
        SwaggerBundle bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();

        String expectedApiDoc = new String(Files.readAllBytes(Paths.get(getClass().getResource(bundleApiDoc.swaggerFile).toURI())));

        doReturn(Optional.of(bundleApiDoc)).when(bundleApiDocsMock).findByKey("default");

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDoc();
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/x-yaml");
        assertThat(toString(result)).isEqualTo(expectedApiDoc);

        verify(bundleApiDocsMock).findByKey("default");
    }

    @Test
    public void shouldReturnHttpConflict_whenBundleApiDocServiceFindByKeyThrowsIllegalStateException() {
        String key = "tomSearle";

        doThrow(new IllegalStateException()).when(bundleApiDocsMock).findByKey(key);

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDocByKey(key);
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(CONFLICT);

        verify(bundleApiDocsMock).findByKey(key);
    }

    @Test
    public void shouldCallBundleApiDocServiceAndReturnHttpNotFound_whenApiDocDoesntExistForKey() {
        String key = "tomSearle";

        doReturn(Optional.empty()).when(bundleApiDocsMock).findByKey(key);

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDocByKey(key);
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        verify(bundleApiDocsMock).findByKey(key);
    }

    @Test
    public void shouldCallBundleApiDocServiceAndReturnHttpOK_whenApiDocExistsForKey() throws Exception {
        String key = "tomSearle";
        SwaggerBundle bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();

        String expectedApiDoc = new String(Files.readAllBytes(Paths.get(getClass().getResource(bundleApiDoc.swaggerFile).toURI())));

        doReturn(Optional.of(bundleApiDoc)).when(bundleApiDocsMock).findByKey(key);

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDocByKey(key);
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/x-yaml");
        assertThat(toString(result)).isEqualTo(expectedApiDoc);

        verify(bundleApiDocsMock).findByKey(key);
    }

    @Test
    public void shouldCallBundleApiDocServiceAdd_onBundleArrival() {
        SwaggerBundle bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();

        swaggerDocController.onBundleArrival(bundleApiDoc.bundle, bundleApiDoc.swaggerFile);

        verify(bundleApiDocsMock).addBundle(bundleApiDoc.bundle, bundleApiDoc.swaggerFile);
    }

    @Test
    public void shouldCallBundleApiDocServiceRemove_onBundleDeparture() {
        SwaggerBundle bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();

        swaggerDocController.onBundleDeparture(bundleApiDoc.bundle);

        verify(bundleApiDocsMock).removeBundle(bundleApiDoc.bundle);
    }
}