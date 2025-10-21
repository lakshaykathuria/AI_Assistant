package com.spring.aiproject.Spring.AI.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class Users {

    @Id
    private String username;

    private String password;

    public Users() {}

    public Users(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
