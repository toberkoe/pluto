package de.toberkoe.pluto.extensions.integration.persistence.config.database;

import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourceProvider {

    String hibernateDialect();

    DataSource dataSource();

    Class<? extends DataSource> dataSourceClassName();

    Properties dataSourceProperties();

    String url();

    String username();

    String password();

    Database database();

    enum IdentifierStrategy {
        IDENTITY,
        SEQUENCE
    }
}