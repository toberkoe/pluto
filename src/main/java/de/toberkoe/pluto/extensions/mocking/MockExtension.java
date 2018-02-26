package de.toberkoe.pluto.extensions.mocking;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockitoAnnotations;

/**
 * Extension for unit tests to simplify mocking.
 * By using this extension you get rid of {@code MockitoAnnotations.initMocks()}
 *
 * @author t.bertram-koehler
 */
public class MockExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        context.getTestInstance().ifPresent(MockitoAnnotations::initMocks);
    }
}
