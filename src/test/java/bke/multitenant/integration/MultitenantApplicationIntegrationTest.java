package bke.multitenant.integration;

import bke.multitenant.config.TenantIdentifierContext;
import bke.multitenant.filter.TenantIdentifierFilter;
import bke.multitenant.model.master.Tenant;
import bke.multitenant.model.tenant.Book;
import bke.multitenant.repository.master.TenantRepository;
import bke.multitenant.repository.tenant.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
public class MultitenantApplicationIntegrationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private BookRepository bookRepository;

    private static boolean databasePopulated = false;

    @BeforeEach
    public void setup() {
        if (databasePopulated) {
            return;
        }

        addTenant("tenant1");
        addTenant("tenant2");

        TenantIdentifierContext.setCurrentTenantId("tenant1");
        Book book = new Book();
        book.setTitle("Book for Tenant1");
        bookRepository.save(book);

        TenantIdentifierContext.setCurrentTenantId("tenant2");
        Book book2 = new Book();
        book2.setTitle("Book for Tenant2");
        bookRepository.save(book2);

        databasePopulated = true;
    }

    @Test
    public void shouldReturnBooksForTenant1() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set(TenantIdentifierFilter.TENANT_IDENTIFIER_HEADER, "tenant1");
        ResponseEntity<Book[]> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/books",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Book[].class
        );

        Book[] books = responseEntity.getBody();
        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, books.length);
        Assertions.assertEquals("Book for Tenant1", books[0].getTitle());
    }

    @Test
    public void shouldReturnBooksForTenant2() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set(TenantIdentifierFilter.TENANT_IDENTIFIER_HEADER, "tenant2");
        ResponseEntity<Book[]> responseEntity = restTemplate.exchange(
                "http://localhost:" + port + "/books",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Book[].class
        );

        Book[] books = responseEntity.getBody();
        Assertions.assertNotNull(books);
        Assertions.assertEquals(1, books.length);
        Assertions.assertEquals("Book for Tenant2", books[0].getTitle());
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
}
