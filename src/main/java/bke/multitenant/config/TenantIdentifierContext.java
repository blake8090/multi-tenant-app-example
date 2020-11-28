package bke.multitenant.config;

public class TenantIdentifierContext {
    private static final ThreadLocal<String> currentTenantId = new ThreadLocal<>();

    public static String getCurrentTenantId() {
        return currentTenantId.get();
    }

    public static void setCurrentTenantId(String tenantId) {
        currentTenantId.set(tenantId);
    }
}
