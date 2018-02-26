package de.toberkoe.pluto.extensions.integration.persistence;

import de.toberkoe.pluto.extensions.integration.persistence.config.PersistenceConfig;
import de.toberkoe.pluto.extensions.integration.persistence.config.PersistenceManager;
import de.toberkoe.pluto.extensions.mocking.MockExtension;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class PersistenceExtension extends MockExtension implements BeforeAllCallback, AfterAllCallback {

    private final PersistenceManager manager;

    public PersistenceExtension() {
        this.manager = new PersistenceManager();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        PersistenceConfig config = PersistenceConfig.build(context.getRequiredTestClass());
        manager.init(config);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        super.beforeEach(context);
        //FIXME speed up by explicit naming of injectable services
        manager.injectAll(context.getTestInstance());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        manager.close();
    }
}
