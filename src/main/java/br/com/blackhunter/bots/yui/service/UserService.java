package br.com.blackhunter.bots.yui.service;

import br.com.blackhunter.bots.yui.entity.User;
import br.com.blackhunter.bots.yui.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));
    }

    public User create(User user) {
        if (user.getId() != null && userRepository.existsById(user.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this ID already exists.");
        }
        return userRepository.save(user);
    }

    public User update(UUID id, User updatedUser) {
        User existingUser = findById(id);
        existingUser.setName(updatedUser.getName());
        existingUser.setHunterId(updatedUser.getHunterId());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPixKey(updatedUser.getPixKey());
        existingUser.setLastLogin(updatedUser.getLastLogin());
        existingUser.setInstances(updatedUser.getInstances());
        existingUser.setPlataforms(updatedUser.getPlataforms());
        return userRepository.save(existingUser);
    }

    public void delete(UUID id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id);
        }
    }
}
