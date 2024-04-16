package ru.mai.khasanov.cryptowebmessenger.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mai.khasanov.cryptowebmessenger.Models.Room;
import ru.mai.khasanov.cryptowebmessenger.Models.User;
import ru.mai.khasanov.cryptowebmessenger.Repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void addRoomToUser(User user, Room room) {
        user.getRooms().add(room);
        userRepository.save(user);
    }
}
