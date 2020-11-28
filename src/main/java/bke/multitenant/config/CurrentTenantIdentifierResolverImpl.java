package bke.multitenant.config;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT_ID = "tenant1";

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantIdentifierContext.getCurrentTenantId();
        return StringUtils.defaultIfBlank(tenantId, DEFAULT_TENANT_ID);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
