package com.livingobjects.wisdom.swagger.internal.bundledoc;

import com.google.common.base.Strings;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

@Component(name = SwaggerDocConfigService.NAME, service = SwaggerDocConfigService.class)
public class SwaggerDocConfigService {
    public static final String NAME = "swagger-doc-config";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SwaggerDocConfigService.class);

    private SwaggerDocConfig config;

    @Activate
    public void activate(SwaggerDocConfig config) {
        this.config = config;

        if (!Strings.isNullOrEmpty(config.defaultBundleName())) {
            LOGGER.info("The default bundle for api doc is {}", config.defaultBundleName());
        }
    }

    public String defaultBundleName() {
        return this.config.defaultBundleName();
    }
}
