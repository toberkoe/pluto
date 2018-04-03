package de.toberkoe.pluto.extensions.integration.persistence;

import de.toberkoe.pluto.extensions.integration.persistence.config.PersistenceConfig;
import de.toberkoe.pluto.extensions.integration.persistence.config.PersistenceManager;
import de.toberkoe.pluto.extensions.mocking.MockExtension;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class PersistenceExtension extends MockExtension implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        PersistenceConfig config = PersistenceConfig.build(context.getRequiredTestClass());
        PersistenceManager.INSTANCE.init(config);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        super.beforeEach(context);
        //FIXME speed up by explicit naming of injectable services
        PersistenceManager.INSTANCE.injectAll(context.getTestInstance());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        PersistenceManager.INSTANCE.close();
    }
}
