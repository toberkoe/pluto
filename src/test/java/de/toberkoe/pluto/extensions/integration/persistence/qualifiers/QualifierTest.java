package de.toberkoe.pluto.extensions.integration.persistence.qualifiers;

import de.toberkoe.pluto.extensions.integration.persistence.PersistenceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.function.Function;

import static de.toberkoe.fluentassertions.api.Assertions.assertThat;

@ExtendWith(PersistenceExtension.class)
class QualifierTest {

    @Inject
    @AppDataSource
    private EntityManager appManager;

    @Inject
    @ExternalDataSource
    private EntityManager externalManager;

    @Test
    void isQualifierResolved() {
        assertThat(appManager).isNotSameAs(externalManager);

        Function<EntityManager, String> unitName = em -> em.getEntityManagerFactory().getProperties().get("hibernate.ejb.persistenceUnitName").toString();
        assertThat(unitName.apply(appManager)).isEqualToIgnoringCase(AppDataSource.class.getName());
        assertThat(unitName.apply(externalManager)).isEqualToIgnoringCase(ExternalDataSource.class.getName());
    }
}
