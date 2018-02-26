package de.toberkoe.pluto.extensions.integration.persistence.config.database;

public enum Database {
    HSQLDB;

    //FIXME create more flexible datasource
    //FIXME support oracle

    public DataSourceProvider dataSourceProvider() {
        switch (this) {
            case HSQLDB:
                return new HSQLDBDataSourceProvider();
            default:
                throw new UnsupportedOperationException("[" + this + "] DataSourceProvider not yet implemented");
        }
    }
}