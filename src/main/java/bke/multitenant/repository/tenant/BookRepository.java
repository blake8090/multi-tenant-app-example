package bke.multitenant.repository.tenant;

import bke.multitenant.model.tenant.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    Book findByTitle(String title);
}
