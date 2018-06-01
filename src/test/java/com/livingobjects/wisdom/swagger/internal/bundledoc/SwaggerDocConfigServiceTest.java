package com.livingobjects.wisdom.swagger.internal.bundledoc;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public final class SwaggerDocConfigServiceTest {

    private SwaggerDocConfigService swaggerDocConfigService;

    @Before
    public void setUp() {
        swaggerDocConfigService = new SwaggerDocConfigService();
    }

    @Test
    public void shouldGetDefaultBundleName() {
        SwaggerDocConfig config = mock(SwaggerDocConfig.class);
        doReturn("default").when(config).defaultBundleName();

        swaggerDocConfigService.activate(config);
        String defaultBundleName = swaggerDocConfigService.defaultBundleName();

        assertThat(defaultBundleName).isEqualTo(config.defaultBundleName());
    }
}