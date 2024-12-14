package weight_assistant_mobile_app_backend.repository;

class MemoryUserRepositoryTest {
    MemoryRepository repository = new MemoryUserRepository();

    @Test
    public void save(){
        User user =new User();
        user.setEmail("abcd@efg.com");

        repository.save(user);

        User result = repository.findById(user.getId()).get();
        Assertions.assertEquals(user, result);
    }
}