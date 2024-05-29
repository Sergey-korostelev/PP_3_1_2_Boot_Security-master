package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository dao;

    public UserServiceImpl(UserRepository dao) {
        this.dao = dao;
    }

    public User findByUsername(String username) {
        return dao.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Not found user '%s'", username));
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), user.getAuthorities());
    }

    public Optional<User> findById(Long id) {
        return dao.findById(id);
    }

    public List<User> findAll() {
        return dao.findAll();
    }

    public void saveUser(User user) {
        dao.save(user);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public void mergeUser(User userId) {
        User dbUser = findById(userId.getId()).orElse(null);
        if (dbUser != null) {
            dbUser.setUsername(userId.getUsername());
            dbUser.setPassword(userId.getPassword());
            dbUser.setRoles(userId.getRoles());
            saveUser(dbUser);
        }
    }


}
