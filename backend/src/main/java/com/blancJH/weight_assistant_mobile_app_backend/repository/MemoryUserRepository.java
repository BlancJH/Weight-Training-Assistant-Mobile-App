import com.blancJH.weight_assistant_mobile_app_backend.repository.UserRepository; 

import java.util.Optional;

public class MemoryUserRepository implements UserRepository {

    private static Map<String, User> store = new Hashmap<>();
    private static long sequence = 0L;

    @Override
    public User save(User user) {
        user.setId(++sequence);
        store.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return store.value().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return store.value().stream()
                .filter(user -> user.getUsername().equals(username))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return new Array<>(store.value());
    }
}   