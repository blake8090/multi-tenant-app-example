package bke.multitenant.repository.master;

import bke.multitenant.model.master.Tenant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends CrudRepository<Tenant, Long> {
    Tenant findByTenantId(String tenantId);
}
