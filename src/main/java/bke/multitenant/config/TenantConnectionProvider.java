package bke.multitenant.config;

import bke.multitenant.model.master.Tenant;
import bke.multitenant.repository.master.TenantRepository;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.Driver;

public class TenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = 1L;

    @Autowired
    private TenantRepository tenantRepository;

    @Override
    protected DataSource selectAnyDataSource() {
        // todo: cache
        Iterable<Tenant> all = tenantRepository.findAll();
        Tenant tenant = all.iterator().next();
        try {
            return createDataSource(tenant);
        } catch (ClassNotFoundException e) {
            // todo: handle error properly
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        if (StringUtils.isBlank(tenantIdentifier)) {
            throw new IllegalArgumentException("Tenant identifier was not provided");
        }

        Tenant tenant = tenantRepository.findByTenantId(tenantIdentifier);

        if (tenant == null) {
            throw new IllegalArgumentException("No tenant found with id " + tenantIdentifier);
        }

        try {
            return createDataSource(tenant);
        } catch (ClassNotFoundException e) {
            // todo: handle error properly
            e.printStackTrace();
            return null;
        }
    }

    private DataSource createDataSource(Tenant tenant) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(tenant.getDatabaseDriverClass());
        Object driver = BeanUtils.instantiateClass(clazz);

        return new SimpleDriverDataSource(
                (Driver) driver,
                tenant.getDatabaseUrl(),
                tenant.getDatabaseUsername(),
                tenant.getDatabasePassword()
        );
    }
}
