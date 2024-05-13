package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> allUsers();
    User saveUser(User user);
    void deleteById(Long id);
    User getById(Long id);

    Optional<User> findByUsername(String username);
}
