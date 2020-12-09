package bke.multitenant.integration;

import bke.multitenant.config.TenantIdentifierContext;
import bke.multitenant.filter.TenantIdentifierFilter;
import bke.multitenant.model.master.Tenant;
import bke.multitenant.model.tenant.Book;
import bke.multitenant.repository.master.TenantRepository;
import bke.multitenant.repository.tenant.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("integration-test")
public class MultitenantApplicationIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private BookRepository bookRepository;

    @Test
    public void shouldReturnBooksForCorrectTenant() {
        String tenantId1 = "tenant1";
        String tenantId2 = "tenant2";
        String book1 = "Book for Tenant1";
        String book2 = "Book for Tenant2";

        addTenant(tenantId1);
        addTenant(tenantId2);

        addBook(tenantId1, book1);
        addBook(tenantId2, book2);

        Book[] tenant1Books = getBooksFromTenant(tenantId1);
        Book[] tenant2Books = getBooksFromTenant(tenantId2);

        Assertions.assertNotNull(tenant1Books);
        Assertions.assertEquals(1, tenant1Books.length);
        Assertions.assertEquals(book1, tenant1Books[0].getTitle());

        Assertions.assertNotNull(tenant2Books);
        Assertions.assertEquals(1, tenant2Books.length);
        Assertions.assertEquals(book2, tenant2Books[0].getTitle());
    }

    private Book[] getBooksFromTenant(String tenantId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set(TenantIdentifierFilter.TENANT_IDENTIFIER_HEADER, tenantId);
        ResponseEntity<Book[]> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/books",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Book[].class
        );

        return responseEntity.getBody();
    }

    private void addTenant(String tenantId) {
        Tenant tenant = new Tenant();
        tenant.setDatabaseDriverClass("org.h2.Driver");
        tenant.setDatabaseUrl("jdbc:h2:mem:" + tenantId);
        tenant.setDatabaseUsername("sa");
        tenant.setDatabasePassword("");
        tenant.setTenantId(tenantId);
        tenantRepository.save(tenant);
    }

    private void addBook(String tenantId, String title) {
        TenantIdentifierContext.setCurrentTenantId(tenantId);
        Book book = new Book();
        book.setTitle(title);
        bookRepository.save(book);
    }
}
