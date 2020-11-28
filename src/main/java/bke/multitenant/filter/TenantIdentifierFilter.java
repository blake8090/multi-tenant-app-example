package bke.multitenant.filter;

import bke.multitenant.config.TenantIdentifierContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TenantIdentifierFilter extends OncePerRequestFilter {

    public static final String TENANT_IDENTIFIER_HEADER = "Tenant-Identifier";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tenantId = request.getHeader(TENANT_IDENTIFIER_HEADER);
        if (StringUtils.isBlank(tenantId)) {
            throw new IllegalArgumentException("Tenant identifier was not provided");
        }

        System.out.println("Using tenant id: " + tenantId);
        TenantIdentifierContext.setCurrentTenantId(tenantId);

        filterChain.doFilter(request, response);
    }
}
