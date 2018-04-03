package de.toberkoe.pluto.extensions.integration.persistence.config.discovery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StaticEntityClassResolverTest {

    @Test
    void resolve() {
        StaticEntityClassResolver resolver = new StaticEntityClassResolver(true, "");
        assertThrows(EntityDiscoveryException.class, () -> resolver.resolve(String.class));
    }
}