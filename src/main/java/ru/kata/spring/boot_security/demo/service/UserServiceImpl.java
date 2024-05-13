package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Transactional(readOnly = true)
    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user) {
        Optional<User> exUser = userRepository.findById(user.getId());
        if (exUser.isPresent()) {
            editUser(user, exUser);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
        return user;
    }

    @Transactional
    public void editUser(User changedUser, Optional<User> presentUser) {
        if (presentUser.isPresent()) {
            if (!changedUser.getPassword().equals(presentUser.get().getPassword())) {
                changedUser.setPassword(passwordEncoder.encode(changedUser.getPassword()));
            }
            userRepository.save(changedUser);
        }
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UsernameNotFoundException("User with ID " + id + " not found");
        }
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}