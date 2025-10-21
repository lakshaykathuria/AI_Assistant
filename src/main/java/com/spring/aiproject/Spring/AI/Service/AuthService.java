package com.spring.aiproject.Spring.AI.Service;

import com.spring.aiproject.Spring.AI.Entity.Users;
import com.spring.aiproject.Spring.AI.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    public void createUser(Users user){
        usersRepository.save(user);
    }

    public boolean validateUser(String username, String password) {
        Users user = usersRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public boolean userExists(String username) {
        return usersRepository.existsById(username);
    }

}

