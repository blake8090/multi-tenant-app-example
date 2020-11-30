package bke.multitenant.config;

import bke.multitenant.model.master.Tenant;
import bke.multitenant.repository.master.TenantRepository;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.StreamSupport;

public class TenantConnectionProvider extends AbstractMultiTenantConnectionProvider {

    private final TenantRepository tenantRepository;

    private Map<String, ConnectionProvider> connectionProviderMap = new HashMap<>();

    @Autowired
    public TenantConnectionProvider(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return getConnectionProviderMap()
                .values()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String identifier) {
        return getConnectionProviderMap().get(identifier);
    }

    private Map<String, ConnectionProvider> getConnectionProviderMap() {
        if (connectionProviderMap.isEmpty()) {
            Iterable<Tenant> tenants = tenantRepository.findAll();
            StreamSupport.stream(tenants.spliterator(), false)
                    .forEach(this::addConnectionProvider);
        }
        return connectionProviderMap;
    }

    private void addConnectionProvider(Tenant tenant) {
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.driver_class", tenant.getDatabaseDriverClass());
        properties.setProperty("hibernate.connection.url", tenant.getDatabaseUrl());
        properties.setProperty("hibernate.connection.username", tenant.getDatabaseUsername());
        properties.setProperty("hibernate.connection.password", tenant.getDatabasePassword());

        DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl();
        connectionProvider.configure(properties);

        connectionProviderMap.put(tenant.getTenantId(), connectionProvider);
    }
}
