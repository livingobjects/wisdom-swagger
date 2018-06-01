package com.livingobjects.wisdom.swagger.internal.rest;

import com.livingobjects.wisdom.swagger.internal.bundledoc.BundleApiDoc;
import com.livingobjects.wisdom.swagger.internal.bundledoc.BundleApiDocService;
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

    private BundleApiDocService bundleApiDocServiceMock;
    private SwaggerDocController swaggerDocController;

    @Before
    public void setUp() {
        bundleApiDocServiceMock = mock(BundleApiDocService.class);

        swaggerDocController = new SwaggerDocController();
        swaggerDocController.setBundleApiDocService(bundleApiDocServiceMock);
    }

    @Test
    public void shouldCallBundleApiDocServiceAndReturnHttpNotFound_whenApiDocDoesntExist() {
        doReturn(Optional.empty()).when(bundleApiDocServiceMock).findDefault();

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDoc();
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        verify(bundleApiDocServiceMock).findDefault();
    }

    @Test
    public void shouldCallBundleApiDocServiceAndReturnHttpOk_whenApiDocExists() throws Exception {
        BundleApiDoc bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();

        String expectedApiDoc = new String(Files.readAllBytes(Paths.get(getClass().getResource(bundleApiDoc.swaggerFile).toURI())));

        doReturn(Optional.of(bundleApiDoc)).when(bundleApiDocServiceMock).findDefault();

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDoc();
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/x-yaml");
        assertThat(toString(result)).isEqualTo(expectedApiDoc);

        verify(bundleApiDocServiceMock).findDefault();
    }

    @Test
    public void shouldReturnHttpConflict_whenBundleApiDocServiceFindByKeyThrowsIllegalStateException() {
        String key = "tomSearle";

        doThrow(new IllegalStateException()).when(bundleApiDocServiceMock).findByKey(key);

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDocByKey(key);
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(CONFLICT);

        verify(bundleApiDocServiceMock).findByKey(key);
    }

    @Test
    public void shouldCallBundleApiDocServiceAndReturnHttpNotFound_whenApiDocDoesntExistForKey() {
        String key = "tomSearle";

        doReturn(Optional.empty()).when(bundleApiDocServiceMock).findByKey(key);

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDocByKey(key);
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(NOT_FOUND);

        verify(bundleApiDocServiceMock).findByKey(key);
    }

    @Test
    public void shouldCallBundleApiDocServiceAndReturnHttpOK_whenApiDocExistsForKey() throws Exception {
        String key = "tomSearle";
        BundleApiDoc bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();

        String expectedApiDoc = new String(Files.readAllBytes(Paths.get(getClass().getResource(bundleApiDoc.swaggerFile).toURI())));

        doReturn(Optional.of(bundleApiDoc)).when(bundleApiDocServiceMock).findByKey(key);

        Action.ActionResult result = Action.action(new Invocation() {
            @Override
            public Result invoke() {
                return swaggerDocController.apiDocByKey(key);
            }
        }).invoke();

        assertThat(status(result)).isEqualTo(OK);
        assertThat(contentType(result)).isEqualTo("application/x-yaml");
        assertThat(toString(result)).isEqualTo(expectedApiDoc);

        verify(bundleApiDocServiceMock).findByKey(key);
    }

//    @Test
//    public void shouldCallBundleApiDocServiceAdd_onBundleArrival() {
//        BundleApiDoc bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();
//
//        swaggerDocController.onBundleArrival(bundleApiDoc.bundle, bundleApiDoc.swaggerFile);
//
//        verify(bundleApiDocServiceMock).addBundle(bundleApiDoc.bundle, bundleApiDoc.swaggerFile);
//    }
//
//    @Test
//    public void shouldCallBundleApiDocServiceRemove_onBundleDeparture() {
//        BundleApiDoc bundleApiDoc = BundleApiDocLibrary.bundleApiDocTom();
//
//        swaggerDocController.onBundleDeparture(bundleApiDoc.bundle);
//
//        verify(bundleApiDocServiceMock).removeBundle(bundleApiDoc.bundle);
//    }
}