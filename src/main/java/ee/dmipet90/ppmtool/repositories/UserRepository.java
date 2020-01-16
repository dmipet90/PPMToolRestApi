package ee.dmipet90.ppmtool.repositories;

import ee.dmipet90.ppmtool.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User getById(Long id);
}
