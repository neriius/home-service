package src.nerius.com.db.controllers;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.nerius.com.db.entity.User;
import src.nerius.com.db.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserRepositoryController {

    private final UserRepository userRepository;

    public UserRepositoryController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    @Transactional(readOnly = true)
    public boolean existsByLogin(String login) {
        return userRepository.existsUserByLogin(login);
    }
}
