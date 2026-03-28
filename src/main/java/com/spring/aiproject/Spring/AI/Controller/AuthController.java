package com.spring.aiproject.Spring.AI.Controller;

import com.spring.aiproject.Spring.AI.Entity.Users;
import com.spring.aiproject.Spring.AI.Service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login (@RequestBody Map<String, String> credentials){
        String username = credentials.get("username");
        String password = credentials.get("password");
        log.info("[AUTH] Login attempt for username='{}'", username);

        boolean valid = authService.validateUser(username, password);

        if(valid){
            log.info("[AUTH] Login successful for username='{}'", username);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message","Login Successful"
            ));
        }else {
            log.warn("[AUTH] Login failed for username='{}' — invalid credentials", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false,
                            "message", "Invalid Username or Password"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup (@RequestBody Map<String, String> body){
        String username = body.get("username");
        String password = body.get("password");
        log.info("[AUTH] Signup attempt for username='{}'", username);

        if(username==null || password==null||username.isEmpty()||password.isEmpty() ){
            log.warn("[AUTH] Signup rejected — missing username or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false,
                            "message","Username and password are required"));
        }

        boolean exists = authService.userExists(username);

        if(exists){
            log.warn("[AUTH] Signup rejected — username='{}' already exists", username);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("success", false,
                            "message","Username already exists"));
        }

        Users users = new Users(username, password);
        authService.createUser(users);
        log.info("[AUTH] User created successfully: username='{}'", username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("success", true,
                        "message","SignUp Successful"));
    }
}
