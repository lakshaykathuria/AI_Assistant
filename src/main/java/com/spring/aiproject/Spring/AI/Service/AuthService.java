package com.spring.aiproject.Spring.AI.Service;

import com.spring.aiproject.Spring.AI.Entity.Users;
import com.spring.aiproject.Spring.AI.Repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    public void createUser(Users user){
        log.debug("[AUTH-SVC] Saving new user: username='{}'", user.getUsername());
        usersRepository.save(user);
    }

    public boolean validateUser(String username, String password) {
        log.debug("[AUTH-SVC] Validating credentials for username='{}'", username);
        Users user = usersRepository.findByUsername(username);
        boolean valid = user != null && user.getPassword().equals(password);
        log.debug("[AUTH-SVC] Validation result for username='{}': {}", username, valid);
        return valid;
    }

    public boolean userExists(String username) {
        boolean exists = usersRepository.existsById(username);
        log.debug("[AUTH-SVC] userExists('{}') = {}", username, exists);
        return exists;
    }

}

